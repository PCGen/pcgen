/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.datacontrol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import pcgen.cdom.content.DefaultVarValue;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.modifier.number.SetModifierFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultVariableValueTokenTest extends
        AbstractTokenTestCase<DefaultVarValue>
{

    private static final plugin.modifier.orderedpair.SetModifierFactory SET_OP =
            new plugin.modifier.orderedpair.SetModifierFactory();
    private static final plugin.modifier.string.SetModifierFactory SET_STRING =
            new plugin.modifier.string.SetModifierFactory();
    private static final SetModifierFactory SET_NUMBER =
            new plugin.modifier.number.SetModifierFactory();
    private static DefaultVariableValueToken token =
            new DefaultVariableValueToken();
    private static CDOMTokenLoader<DefaultVarValue> loader =
            new CDOMTokenLoader<>();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(SET_NUMBER);
        TokenRegistration.register(SET_STRING);
        TokenRegistration.register(SET_OP);
    }

    @Test
    public void testInvalidInputNullString()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, null)
                .passed());
    }

    @Test
    public void testInvalidInputEmptyString()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, "").passed());
    }

    @Test
    public void testInvalidInputNotAType()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, "BADTYPE|45")
                .passed());
    }

    @Test
    public void testInvalidInputEmptyType()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, "|3")
                .passed());
    }

    @Test
    public void testInvalidTypeValue()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, "NUMBER|3r")
                .passed());
    }

    @Test
    public void testInvalidInputDoublePipe()
    {
        assertFalse(token
                .parseToken(primaryContext, primaryProf, "STRING||Def").passed());
    }

    @Test
    public void testInvalidInputTooManyArgs()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf,
                "STRING|Def|Abc").passed());
    }

    @Test
    public void testInvalidDefaultEmptyNumber()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, "NUMBER|")
                .passed());
    }

    @Test
    public void testValidDefultEmptyString()
    {
        assertTrue(token.parseToken(primaryContext, primaryProf, "STRING|")
                .passed());
        String[] unparsed = token.unparse(primaryContext, primaryProf);
        assertNotNull(unparsed);
        assertEquals(1, unparsed.length);
        assertEquals("STRING|", unparsed[0]);
    }

    @Test
    public void testValidStringNo()
    {
        ParseResult pr =
                token
                        .parseToken(primaryContext, primaryProf, "ORDEREDPAIR|0,3");
        if (!pr.passed())
        {
            fail(pr.toString());
        }
        String[] unparsed = token.unparse(primaryContext, primaryProf);
        assertNotNull(unparsed);
        assertEquals(1, unparsed.length);
        assertEquals("ORDEREDPAIR|0,3", unparsed[0]);
    }

    @Override
    public Class<? extends DefaultVarValue> getCDOMClass()
    {
        return DefaultVarValue.class;
    }

    @Override
    public void isCDOMEqual(DefaultVarValue cdo1, DefaultVarValue cdo2)
    {
        if (!Objects.equals(cdo1.getKeyName(), cdo2.getKeyName()))
        {
            fail("Mismatched");
        }
    }

    @Override
    public CDOMLoader<DefaultVarValue> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<DefaultVarValue> getToken()
    {
        return token;
    }

    @Override
    protected String getLegalValue()
    {
        return "NUMBER|3";
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "ORDEREDPAIR|0,3";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Override
    protected void additionalSetup(LoadContext context)
    {
        URI testURI = testCampaign.getURI();
        context.setSourceURI(testURI);
        context.setExtractURI(testURI);
        context.getReferenceContext().importObject(BuildUtilities.getFeatCat());
    }
}

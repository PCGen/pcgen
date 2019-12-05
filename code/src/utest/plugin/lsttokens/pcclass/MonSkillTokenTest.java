/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.pcclass;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.bonustokens.MonNonSkillHD;
import plugin.bonustokens.MonSkillPts;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreLevelMaxParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreRaceWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MonSkillTokenTest extends AbstractCDOMTokenTestCase<PCClass>
{

    static MonskillToken token = new MonskillToken();
    static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<>();

    PreRaceParser prerace = new PreRaceParser();
    PreRaceWriter preracewriter = new PreRaceWriter();
    PreLevelMaxParser prelevelmax = new PreLevelMaxParser();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        addBonus(MonSkillPts.class);
        TokenRegistration.register(prerace);
        TokenRegistration.register(preracewriter);
        TokenRegistration.register(prelevelmax);
    }

    @Override
    public Class<PCClass> getCDOMClass()
    {
        return PCClass.class;
    }

    @Override
    public CDOMLoader<PCClass> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<PCClass> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidInputEmpty()
    {
        try
        {
            assertFalse(parse(""));
        } catch (IllegalArgumentException e)
        {
            // This is Okay too :)
        }
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinOnlyPre()
    {
        assertFalse(parse("PRERACE:1,Human"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinBase() throws PersistenceLayerException
    {
        runRoundRobin("VARIABLE1");
    }

    @Test
    public void testRoundRobinNumber() throws PersistenceLayerException
    {
        runRoundRobin("3");
    }

    @Test
    public void testRoundRobinFormula() throws PersistenceLayerException
    {
        runRoundRobin("3+CL(\"FIGHTER\")");
    }

    @Test
    public void testRoundRobinPre() throws PersistenceLayerException
    {
        runRoundRobin("VARIABLE1|PRERACE:1,HUMAN");
    }

    @Test
    public void testRoundRobinDupePre() throws PersistenceLayerException
    {
        runRoundRobin("VARIABLE1", "VARIABLE1|PRERACE:1,HUMAN");
    }

    @Test
    public void testRoundRobinDiffPre() throws PersistenceLayerException
    {
        runRoundRobin("VARIABLE1|PRERACE:1,DWARF", "VARIABLE1|PRERACE:1,HUMAN");
    }

    @Test
    public void testRoundRobinDiffSamePre() throws PersistenceLayerException
    {
        runRoundRobin("VARIABLE1|PRERACE:1,HUMAN", "VARIABLE2|PRERACE:1,HUMAN");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "VARIABLE2";
    }

    @Override
    protected String getLegalValue()
    {
        return "VARIABLE1|PRERACE:1,HUMAN";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }

    @Test
    public void testOtherBonus()
    {
        addBonus(MonNonSkillHD.class);
        MonnonskillhdToken othertoken = new MonnonskillhdToken();
        TokenRegistration.register(othertoken);
        assertTrue(othertoken.parseToken(primaryContext, primaryProf, "1").passed());
        primaryContext.commit();
        assertNull(token.unparse(primaryContext, primaryProf));
        assertNotNull(primaryContext.unparse(primaryProf));
    }

}

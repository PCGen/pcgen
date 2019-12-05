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
package plugin.lsttokens.sizeadjustment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.KeyLst;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbbTokenTest extends AbstractCDOMTokenTestCase<SizeAdjustment>
{
    static AbbToken token = new AbbToken();
    static KeyLst keyToken = new KeyLst();

    static CDOMTokenLoader<SizeAdjustment> loader = new CDOMTokenLoader<>();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(keyToken);
    }

    @Override
    public Class<SizeAdjustment> getCDOMClass()
    {
        return SizeAdjustment.class;
    }

    @Override
    public CDOMLoader<SizeAdjustment> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<SizeAdjustment> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidInputEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testValidInputs()
    {
        assertTrue(parse("Niederösterreich"));
        assertEquals("Niederösterreich", primaryProf.get(StringKey.ABB_KR));
        assertTrue(parse("Finger Lakes"));
        assertEquals("Finger Lakes", primaryProf.get(StringKey.ABB_KR));
        assertTrue(parse("Rheinhessen"));
        assertEquals("Rheinhessen", primaryProf.get(StringKey.ABB_KR));
        assertTrue(parse("Languedoc-Roussillon"));
        assertEquals("Languedoc-Roussillon", primaryProf.get(StringKey.ABB_KR));
        assertTrue(parse("Yarra Valley"));
        assertEquals("Yarra Valley", primaryProf.get(StringKey.ABB_KR));
    }

    @Test
    public void testReplacementInputs()
    {
        String[] unparsed;
        assertTrue(parse("Start"));
        assertTrue(parse("Mod"));
        unparsed = getToken().unparse(primaryContext, primaryProf);
        assertEquals("Mod", unparsed[0]);
    }

    @Test
    public void testRoundRobinBase() throws PersistenceLayerException
    {
        runRoundRobin("Rheinhessen");
    }

    @Test
    public void testRoundRobinWithSpace() throws PersistenceLayerException
    {
        runRoundRobin("Finger Lakes");
    }

    @Test
    public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
    {
        runRoundRobin("Niederösterreich");
    }

    @Test
    public void testRoundRobinHyphen() throws PersistenceLayerException
    {
        runRoundRobin("Languedoc-Roussillon");
    }

    @Test
    public void testRoundRobinY() throws PersistenceLayerException
    {
        runRoundRobin("Yarra Valley");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Languedoc-Roussillon";
    }

    @Override
    protected String getLegalValue()
    {
        return "Yarra Valley";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Test
    public void testUnparseLegal()
    {
        expectSingle(setAndUnparse(getLegalValue()), getLegalValue());
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(getStringKey(), null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    /*
     * TODO Need to define the appropriate behavior here - is the token
     * responsible for catching this?
     */
    // @Test
    // public void testUnparseEmpty() throws PersistenceLayerException
    // {
    // primaryProf.put(getStringKey(), "");
    // assertBadUnparse();
    // }

    protected String[] setAndUnparse(String val)
    {
        primaryProf.put(getStringKey(), val);
        return getToken().unparse(primaryContext, primaryProf);
    }

    private static StringKey getStringKey()
    {
        return StringKey.ABB_KR;
    }
}

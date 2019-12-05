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
package plugin.lsttokens.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SubRace;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTypeSafeTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class SubraceTokenTest extends
        AbstractTypeSafeTokenTestCase<PCTemplate, SubRace>
{

    static SubraceToken token = new SubraceToken();
    static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
    }

    @Override
    public CDOMLoader<PCTemplate> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<PCTemplate> getToken()
    {
        return token;
    }

    @Override
    public SubRace getConstant(String string)
    {
        return SubRace.getConstant(string);
    }

    @Override
    public ObjectKey<SubRace> getObjectKey()
    {
        return ObjectKey.SUBRACE;
    }

    @Override
    protected boolean requiresPreconstruction()
    {
        return false;
    }

    @Override
    public boolean isClearLegal()
    {
        return false;
    }

    @Test
    public void testRoundRobinYes() throws PersistenceLayerException
    {
        runRoundRobin("YES");
    }

    @Test
    public void testReplacementYes()
    {
        String[] unparsed;
        getConstant("TestWP1");
        assertTrue(parse("YES"));
        unparsed = getToken().unparse(primaryContext, primaryProf);
        assertEquals(1, unparsed.length);
        assertEquals("YES", unparsed[0]);
        assertTrue(parse("TestWP1"));
        unparsed = getToken().unparse(primaryContext, primaryProf);
        assertEquals(1, unparsed.length);
        assertEquals("TestWP1", unparsed[0]);
        assertTrue(parse("YES"));
        unparsed = getToken().unparse(primaryContext, primaryProf);
        assertEquals(1, unparsed.length);
        assertEquals("YES", unparsed[0]);
    }

    @Test
    public void testOverwriteYes()
    {
        parse("YES");
        validateUnparsed(primaryContext, primaryProf, "YES");
        parse("TestWP1");
        validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
                .getAnswer("TestWP1"));
    }

    @Test
    public void testOverwriteWithYes()
    {
        parse("TestWP1");
        validateUnparsed(primaryContext, primaryProf, "TestWP1");
        parse("YES");
        validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
                .getAnswer("YES"));
    }

    @Test
    public void testUnparseYes()
    {
        primaryProf.put(ObjectKey.USETEMPLATENAMEFORSUBRACE, true);
        expectSingle(getToken().unparse(primaryContext, primaryProf), "YES");
    }

    @Test
    public void testUnparseIllegal()
    {
        assertEquals(0, primaryContext.getWriteMessageCount());
        SubRace o = getConstant(getLegalValue());
        primaryProf.put(getObjectKey(), o);
        primaryProf.put(ObjectKey.USETEMPLATENAMEFORSUBRACE, true);
        expectSingle(getToken().unparse(primaryContext, primaryProf), "YES");
        assertTrue(primaryContext.getWriteMessageCount() > 0);
    }

    @Test
    public void testUnparseLegalWithFalse()
    {
        assertEquals(0, primaryContext.getWriteMessageCount());
        SubRace o = getConstant(getLegalValue());
        primaryProf.put(getObjectKey(), o);
        primaryProf.put(ObjectKey.USETEMPLATENAMEFORSUBRACE, false);
        expectSingle(getToken().unparse(primaryContext, primaryProf),
                getLegalValue());
    }
}

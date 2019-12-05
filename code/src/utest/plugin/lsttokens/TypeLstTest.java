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
package plugin.lsttokens;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.PCTemplate;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTypeSafeListTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class TypeLstTest extends AbstractGlobalTypeSafeListTestCase
{

    static TypeLst token = new TypeLst();
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
    public CDOMPrimaryToken<CDOMObject> getReadToken()
    {
        return token;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getWriteToken()
    {
        return token;
    }

    @Override
    public Object getConstant(String string)
    {
        return Type.getConstant(string);
    }

    @Override
    public char getJoinCharacter()
    {
        return '.';
    }

    @Override
    public ListKey<?> getListKey()
    {
        return ListKey.TYPE;
    }

    @Override
    public boolean isClearDotLegal()
    {
        return false;
    }

    @Override
    public boolean isClearLegal()
    {
        return true;
    }

    @Test
    public void testReplacementRemove()
    {
        String[] unparsed;
        assertTrue(parse("REMOVE.TestWP1"));
        unparsed = getWriteToken().unparse(primaryContext, primaryProf);
        assertNull(unparsed);

        assertTrue(parse("TestWP1"));
        assertTrue(parse("ADD.TestWP2"));
        unparsed = getWriteToken().unparse(primaryContext, primaryProf);
        assertEquals("TestWP1"
                + getJoinCharacter() + "TestWP2", unparsed[0]);
        if (isClearLegal())
        {
            assertTrue(parse(Constants.LST_DOT_CLEAR));
            unparsed = getWriteToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
    }

    @Test
    public void testReplacementRemoveTwo()
    {
        String[] unparsed;
        assertTrue(parse("TestWP1"));
        assertTrue(parse("TestWP2"));
        unparsed = getWriteToken().unparse(primaryContext, primaryProf);
        assertEquals("TestWP1"
                + getJoinCharacter() + "TestWP2", unparsed[0]);
        assertTrue(parse("REMOVE.TestWP1"));
        unparsed = getWriteToken().unparse(primaryContext, primaryProf);
        assertEquals("TestWP2", unparsed[0]);
    }

    @Test
    public void testInputInvalidRemoveNoTrailing()
    {
        assertFalse(parse("TestWP1.REMOVE"));
        assertNoSideEffects();
    }

    @Test
    public void testInputInvalidAddNoTrailing()
    {
        assertFalse(parse("TestWP1.ADD"));
        assertNoSideEffects();
    }

    @Test
    public void testInputInvalidAddRemove()
    {
        assertFalse(parse("TestWP1.ADD.REMOVE.TestWP2"));
        assertNoSideEffects();
    }

    @Test
    public void testInputInvalidRemoveAdd()
    {
        assertFalse(parse("TestWP1.REMOVE.ADD.TestWP2"));
        assertNoSideEffects();
    }

    @Test
    public void testInputInvalidEmbeddedClear()
    {
        assertFalse(parse("TestWP1.CLEAR.TestWP2"));
        assertNoSideEffects();
    }

    @Test
    public void testInputInvalidDotClearDot()
    {
        assertFalse(parse(".CLEAR."));
        assertNoSideEffects();
    }

    @Test
    public void testInputInvalidDotClearStuff()
    {
        assertFalse(parse(".CLEARSTUFF"));
        assertNoSideEffects();
    }

    @Override
    protected boolean isAllLegal()
    {
        return false;
    }

    @Override
    protected boolean requiresPreconstruction()
    {
        return false;
    }
}

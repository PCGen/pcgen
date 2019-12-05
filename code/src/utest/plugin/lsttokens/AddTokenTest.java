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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.PCTemplate;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class AddTokenTest extends AbstractGlobalTokenTestCase
{

    static CDOMPrimaryToken<CDOMObject> token = new AddLst();
    static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

    @Override
    public CDOMLoader<PCTemplate> getLoader()
    {
        return loader;
    }

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
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

    @Test
    public void testInvalidEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidClearLevel()
    {
        assertFalse(parse(".CLEAR.LEVEL1"));
        assertNoSideEffects();
    }

    @Test
    public void testValidClear()
    {
        assertTrue(parse(Constants.LST_DOT_CLEAR));
    }

    @Test
    public void testInvalidLevelNonClearLevel()
    {
        primaryProf = new PCClassLevel();
        primaryProf.put(IntegerKey.LEVEL, 1);
        secondaryProf = new PCClassLevel();
        secondaryProf.put(IntegerKey.LEVEL, 1);
        assertFalse(parse(Constants.LST_DOT_CLEAR));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidLevelClearWrongLevel()
    {
        primaryProf = new PCClassLevel();
        primaryProf.put(IntegerKey.LEVEL, 1);
        secondaryProf = new PCClassLevel();
        secondaryProf.put(IntegerKey.LEVEL, 1);
        assertFalse(parse(".CLEAR.LEVEL2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidLevelClearLevelNaN()
    {
        primaryProf = new PCClassLevel();
        primaryProf.put(IntegerKey.LEVEL, 1);
        secondaryProf = new PCClassLevel();
        secondaryProf.put(IntegerKey.LEVEL, 1);
        assertFalse(parse(".CLEAR.LEVELx"));
        assertNoSideEffects();
    }

    @Test
    public void testValidClearLevel()
    {
        primaryProf = new PCClassLevel();
        primaryProf.put(IntegerKey.LEVEL, 1);
        secondaryProf = new PCClassLevel();
        secondaryProf.put(IntegerKey.LEVEL, 1);
        assertTrue(parse(".CLEAR.LEVEL1"));
    }

    @Override
    protected String getAlternateLegalValue()
    {
        // Not worth it, nothing ever unparses
        return null;
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        // Not worth it, nothing ever unparses
        return null;
    }

    @Override
    protected String getLegalValue()
    {
        // Not worth it, nothing ever unparses
        return Constants.LST_DOT_CLEAR;
    }

    @Override
    public void testOverwrite()
    {
        // Can't be done, nothing ever unparses
    }

}

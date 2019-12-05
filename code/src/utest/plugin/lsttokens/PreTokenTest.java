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

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.core.PCTemplate;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class PreTokenTest extends AbstractGlobalTokenTestCase
{

    static CDOMPrimaryToken<ConcretePrereqObject> token = new PreLst();
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
    public CDOMPrimaryToken<ConcretePrereqObject> getReadToken()
    {
        return token;
    }

    @Override
    public CDOMPrimaryToken<ConcretePrereqObject> getWriteToken()
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
    public void testInvalidOther()
    {
        assertFalse(parse("SPELL"));
        assertNoSideEffects();
    }

    @Test
    public void testValidTypeBarOnly()
    {
        assertTrue(parse(Constants.LST_DOT_CLEAR));
    }

    @Override
    protected String getAlternateLegalValue()
    {
        //Not worth it, nothing ever unparses
        return null;
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        //Not worth it, nothing ever unparses
        return null;
    }

    @Override
    protected String getLegalValue()
    {
        //Not worth it, nothing ever unparses
        return Constants.LST_DOT_CLEAR;
    }

    @Override
    public void testOverwrite()
    {
        //Can't be done, nothing ever unparses
    }

}

/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.modifier.string;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.base.calculation.FormulaModifier;
import pcgen.base.format.StringManager;
import pcgen.base.util.FormatManager;
import pcgen.rules.persistence.token.ModifierFactory;

import plugin.modifier.testsupport.EvalManagerUtilities;

import org.junit.jupiter.api.Test;

public class SetStringModifierTest
{

    private FormatManager<String> stringManager = new StringManager();

    @Test
    public void testInvalidConstruction()
    {
        try
        {
            SetModifierFactory m = new SetModifierFactory();
            m.getModifier(null, null);
            fail("Expected SetModifier with null set value to fail");
        } catch (IllegalArgumentException | NullPointerException e)
        {
            //Yep!
        }
    }

    @Test
    public void testGetModifier()
    {
        ModifierFactory<String> factory = new SetModifierFactory();
        FormulaModifier<String> modifier =
                factory.getModifier("MyString", stringManager);
        modifier.addAssociation("PRIORITY=5");
        assertEquals(5L << 32, modifier.getPriority());
        assertEquals(stringManager, modifier.getVariableFormat());
        assertEquals("MyString", modifier.process(EvalManagerUtilities.getInputEM("Wrong Answer")));
    }

}

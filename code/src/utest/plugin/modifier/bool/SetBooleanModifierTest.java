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
package plugin.modifier.bool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import pcgen.base.calculation.FormulaModifier;
import pcgen.base.format.BooleanManager;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.inst.SimpleLegalScope;
import pcgen.base.util.FormatManager;
import pcgen.cdom.formula.scope.GlobalScope;
import pcgen.rules.persistence.token.ModifierFactory;
import plugin.modifier.testsupport.EvalManagerUtilities;

public class SetBooleanModifierTest
{

	private final LegalScope varScope =
			new SimpleLegalScope(GlobalScope.GLOBAL_SCOPE_NAME);
	private FormatManager<Boolean> booleanManager = new BooleanManager();

	@Test
	public void testInvalidConstruction()
	{
		try
		{
			ModifierFactory m = new SetModifierFactory();
			m.getModifier(null, new ManagerFactory(){}, null, null, null);
			fail("Expected SetModifier with null set value to fail");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//Yep!
		}
	}

	@Test
	public void testGetModifier()
	{
		ModifierFactory factory = new SetModifierFactory();
		FormulaModifier<Boolean> modifier =
				factory.getModifier("True", new ManagerFactory(){}, null, varScope, booleanManager);
		modifier.addAssociation("PRIORITY=5");
		assertEquals(5L <<32, modifier.getPriority());
		assertEquals(booleanManager, modifier.getVariableFormat());
		assertEquals(Boolean.TRUE, modifier.process(EvalManagerUtilities.getInputEM(Boolean.FALSE)));
	}

}

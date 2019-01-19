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
package plugin.modifier.orderedpair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.BeforeEach;

import pcgen.base.calculation.FormulaModifier;
import pcgen.base.format.OrderedPairManager;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.math.OrderedPair;
import pcgen.base.util.FormatManager;
import pcgen.cdom.formula.scope.GlobalScope;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.rules.persistence.token.ModifierFactory;
import plugin.modifier.testsupport.EvalManagerUtilities;

public class SetOrderedPairModifierTest
{

	private final PCGenScope varScope = new GlobalScope();
	private FormatManager<OrderedPair> opManager = new OrderedPairManager();

	@Test
	public void testInvalidConstruction()
	{
		try
		{
			SetModifierFactory m = new SetModifierFactory();
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
		ModifierFactory<OrderedPair> factory = new SetModifierFactory();
		FormulaModifier<OrderedPair> modifier =
				factory.getModifier("3,2", new ManagerFactory(){}, null, varScope, opManager);
		modifier.addAssociation("PRIORITY=5");
		assertEquals(5L <<32, modifier.getPriority());
		assertEquals(opManager, modifier.getVariableFormat());
		assertEquals(new OrderedPair(3, 2),
			modifier.process(EvalManagerUtilities.getInputEM(new OrderedPair(5, 6))));
	}

}

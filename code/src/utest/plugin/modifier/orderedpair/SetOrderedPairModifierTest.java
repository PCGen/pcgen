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

import pcgen.base.format.OrderedPairManager;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.inst.SimpleLegalScope;
import pcgen.base.math.OrderedPair;
import pcgen.base.solver.Modifier;
import pcgen.base.util.FormatManager;
import pcgen.rules.persistence.token.ModifierFactory;

import org.junit.Test;
import plugin.modifier.testsupport.EvalManagerUtilities;
import static org.junit.Assert.*;

public class SetOrderedPairModifierTest
{

	private final LegalScope varScope = new SimpleLegalScope(null, "Global");
	private FormatManager<OrderedPair> opManager = new OrderedPairManager();

	@Test
	public void testInvalidConstruction()
	{
		try
		{
			SetModifierFactory m = new SetModifierFactory();
			m.getModifier(100, null, new ManagerFactory(){}, null, null, null);
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
		Modifier<OrderedPair> modifier =
				factory.getModifier(5, "3,2", new ManagerFactory(){}, null, varScope, opManager);
		assertEquals(5l<<32, modifier.getPriority());
		assertSame(OrderedPair.class, modifier.getVariableFormat());
		assertEquals(new OrderedPair(3, 2),
			modifier.process(EvalManagerUtilities.getInputEM(new OrderedPair(5, 6))));
	}

}

/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.solver;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.VariableID;
import pcgen.base.testsupport.AbstractFormulaTestCase;
import pcgen.base.testsupport.TestUtilities.Container;

public class AggressiveStrategyTest extends AbstractFormulaTestCase
{
	@Test
	public void testIllegalConstruction()
	{
		assertThrows(NullPointerException.class, () -> new AggressiveStrategy(null, var -> true));
		assertThrows(NullPointerException.class, () -> new AggressiveStrategy((varID, consumer) -> {}, null));
	}

	@Test
	public void testAddModifierExternal()
	{
		VariableID<Number> parent = new VariableID<>(getGlobalScopeInst(),
				FormatUtilities.NUMBER_MANAGER, "STR");
		VariableID<Number> child = new VariableID<>(getGlobalScopeInst(),
				FormatUtilities.NUMBER_MANAGER, "LIFT");

		Container<VariableID<?>> target = new Container<>();
		ChildProcessor proc = new ChildProcessor(child);

		SolverStrategy strategy = new AggressiveStrategy(proc::processForChildren, target::set);
		
		assertTrue(target.objects.isEmpty());
		strategy.processModsUpdated(parent);
		assertTrue(target.objects.get(0).equals(parent));
		strategy.processValueUpdated(parent);
		assertTrue(target.objects.get(1).equals(child));
		assertTrue(proc.parent.equals(parent));
	}
	
	private class ChildProcessor
	{
		private final VariableID<?> toDrive;
		
		public ChildProcessor(VariableID<?> toDrive)
		{
			this.toDrive = toDrive;
		}

		public VariableID<?> parent;
		
		public void processForChildren(VariableID<?> varID,
			Consumer<VariableID<?>> consumer)
		{
			if (varID.equals(toDrive))
			{
				return;
			}
			parent = varID;
			consumer.accept(toDrive);
		}
		
	}

}

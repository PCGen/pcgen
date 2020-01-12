/*
 * Copyright (c) Thomas Parker, 2019.
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
package pcgen.cdom.formula;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.inst.ComplexNEPFormula;
import pcgen.base.formula.inst.FormulaUtilities;
import pcgen.base.solver.Modifier;
import pcgen.base.solver.SolverManager;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.formula.scope.GlobalPCScope;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.cdom.formula.testsupport.AbstractModifier;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;

import plugin.function.testsupport.AbstractFormulaTestCase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VariableChannelTest extends AbstractFormulaTestCase
{

	private SolverManager manager;
	private ScopeInstance globalInstance;
	private PCGenScope globalScope;

	@Override
	@BeforeEach
	public void setUp() throws Exception
	{
		super.setUp();
		FormulaUtilities.loadBuiltInOperators(getOperatorLibrary());

		manager = context.getVariableContext().generateSolverManager(getVariableStore());
		globalScope = context.getVariableContext().getScope(GlobalPCScope.GLOBAL_SCOPE_NAME);
		ScopeInstanceFactory sif = getFormulaManager().getScopeInstanceFactory();
		globalInstance = sif.getGlobalInstance(globalScope.getName());
	}
	
	@AfterEach
	public void tearDown()
	{
		manager = null;
		globalScope = null;
		globalInstance = null;
	}

	@SuppressWarnings("unused")
	@Test
	void testBadConstructionFirstArg()
	{
		VariableID<Number> varID = new VariableID<>(globalInstance,
			FormatUtilities.NUMBER_MANAGER, "MyNumber");
		assertThrows(NullPointerException.class, () -> VariableChannel.construct(null, getVariableStore(), varID));
	}

	@SuppressWarnings("unused")
	@Test
	void testBadConstructionSecondArg()
	{
		VariableID<Number> varID = new VariableID<>(globalInstance,
				FormatUtilities.NUMBER_MANAGER, "MyNumber");
		DataSetID dsID = DataSetID.getID();
		CharID id = CharID.getID(dsID);
		assertThrows(NullPointerException.class, () -> VariableChannel.construct(manager, null, varID));
	}

	@SuppressWarnings("unused")
	@Test
	void testBadConstructionThirdArg()
	{
		DataSetID dsID = DataSetID.getID();
		CharID id = CharID.getID(dsID);
		assertThrows(NullPointerException.class, () -> VariableChannel.construct(manager, getVariableStore(), null));
	}

	@Test
	void testGetSet()
	{
		VariableID<Number> varID = new VariableID<>(globalInstance,
				FormatUtilities.NUMBER_MANAGER, "MyNumber");
		VariableChannel<Number> channel =
				VariableChannel.construct(manager, getVariableStore(), varID);
		assertEquals(0, channel.get());
		getVariableStore().put(varID, 4);
		assertEquals(4, channel.get());
		channel.set(6);
		assertEquals(6, getVariableStore().get(varID));
	}

	@Test
	void testGetSetEvents()
	{
		VariableID<Number> varID = new VariableID<>(globalInstance,
				FormatUtilities.NUMBER_MANAGER, "MyNumber");
		VariableChannel<Number> channel =
				VariableChannel.construct(manager, getVariableStore(), varID);
		TestingReferenceListener<Number> listener =
				new TestingReferenceListener<>();
		channel.addReferenceListener(listener);
		assertEquals(0, listener.getEventCount());
		assertEquals(0, channel.get());
		assertEquals(0, listener.getEventCount());
		getVariableStore().put(varID, 4);
		assertNull(listener.getLastOld());
		assertEquals(4, listener.getLastNew());
		assertEquals(1, listener.getEventCount());
		assertEquals(4, channel.get());
		channel.set(6);
		assertEquals(2, listener.getEventCount());
		assertEquals(4, listener.getLastOld());
		assertEquals(6, listener.getLastNew());
		assertEquals(6, getVariableStore().get(varID));
	}
	
	private static class TestingReferenceListener<T> implements ReferenceListener<T>
	{

		private int eventCount;
		private T lastOld;
		private T lastNew;

		@Override
		public void referenceChanged(ReferenceEvent<T> e)
		{
			eventCount++;
			lastOld = e.getOldReference();
			lastNew = e.getNewReference();
		}
		
		public int getEventCount()
		{
			return eventCount;
		}

		public T getLastOld()
		{
			return lastOld;
		}

		public T getLastNew()
		{
			return lastNew;
		}
		
	}

	@Test
	void testGetSetEffects()
	{
		ComplexNEPFormula<Number> formula =
				new ComplexNEPFormula<>("arms+legs", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> formulaMod = AbstractModifier.add(formula, 100);
		getVariableLibrary().assertLegalVariableID("Limbs", globalScope,
			FormatUtilities.NUMBER_MANAGER);
		getVariableLibrary().assertLegalVariableID("arms", globalScope,
			FormatUtilities.NUMBER_MANAGER);
		getVariableLibrary().assertLegalVariableID("legs", globalScope,
			FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> limbs =
				(VariableID<Number>) getVariableLibrary().getVariableID(globalInstance, "Limbs");
		manager.addModifier(limbs, formulaMod, globalInstance);

		@SuppressWarnings("unchecked")
		VariableID<Number> arms =
				(VariableID<Number>) getVariableLibrary().getVariableID(globalInstance, "Arms");

		@SuppressWarnings("unchecked")
		VariableID<Number> legs =
				(VariableID<Number>) getVariableLibrary().getVariableID(globalInstance, "Legs");
		VariableChannel<Number> armsChannel =
				VariableChannel.construct(manager, getVariableStore(), arms);
		VariableChannel<Number> legsChannel =
				VariableChannel.construct(manager, getVariableStore(), legs);

		assertEquals(0, getVariableStore().get(legs));
		assertEquals(0, legsChannel.get());
		legsChannel.set(4);
		assertEquals(4, getVariableStore().get(legs));
		assertEquals(4, legsChannel.get());
		assertEquals(4, getVariableStore().get(limbs));

		assertEquals(0, armsChannel.get());
		assertEquals(0, getVariableStore().get(arms));
		armsChannel.set(3);
		assertEquals(3, getVariableStore().get(arms));
		assertEquals(3, armsChannel.get());
		assertEquals(7, getVariableStore().get(limbs));
		armsChannel.set(6);
		assertEquals(6, getVariableStore().get(arms));
		assertEquals(6, armsChannel.get());
		assertEquals(10, getVariableStore().get(limbs));
	}

	//TODO Test for VETO - no side effects
	
}

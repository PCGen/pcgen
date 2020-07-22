/*
 * (C) Copyright 2014 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.inst;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.exception.LegalVariableException;
import pcgen.base.math.OrderedPair;
import pcgen.base.solver.SupplierValueStore;
import pcgen.base.testsupport.SimpleVarScoped;
import pcgen.base.util.Indirect;

public class VariableManagerTest
{

	private ScopeInstanceFactory instanceFactory;
	private ScopeManagerInst legalScopeManager;
	private VariableLibrary variableLibrary;
	private SupplierValueStore valueStore;

	@BeforeEach
	void setUp()
	{
		valueStore = new SupplierValueStore();
		legalScopeManager = new ScopeManagerInst();
		instanceFactory = new SimpleScopeInstanceFactory(legalScopeManager);
		valueStore.addValueFor(FormatUtilities.NUMBER_MANAGER, () -> 0);
		valueStore.addValueFor(FormatUtilities.STRING_MANAGER, () -> "");
		valueStore.addValueFor(FormatUtilities.BOOLEAN_MANAGER, () -> false);
		variableLibrary = new VariableManager(legalScopeManager, valueStore);
	}
	
	@AfterEach
	void tearDown()
	{
		instanceFactory = null;
		legalScopeManager = null;
		variableLibrary = null;
		valueStore = null;
	}

	@Test
	public void testNullConstructor()
	{
		assertThrows(NullPointerException.class, () -> new VariableManager(null, valueStore));
		assertThrows(NullPointerException.class, () -> new VariableManager(legalScopeManager, null));
		assertThrows(NullPointerException.class, () -> new VariableManager(null, null));
	}

	@Test
	public void testAssertVariableFail()
	{
		LegalScope globalScope = new SimpleLegalScope("Global");
		assertThrows(NullPointerException.class, () -> variableLibrary.assertLegalVariableID(null, globalScope, FormatUtilities.NUMBER_MANAGER));
		assertThrows(IllegalArgumentException.class, () -> variableLibrary.assertLegalVariableID("", globalScope, FormatUtilities.NUMBER_MANAGER));
		assertThrows(IllegalArgumentException.class, () -> variableLibrary.assertLegalVariableID(" Walk", globalScope, FormatUtilities.NUMBER_MANAGER));
		assertThrows(IllegalArgumentException.class, () -> variableLibrary.assertLegalVariableID("Walk ", globalScope, FormatUtilities.NUMBER_MANAGER));
		assertThrows(NullPointerException.class, () -> variableLibrary.assertLegalVariableID("Walk", globalScope, null));
		assertThrows(NullPointerException.class, () -> variableLibrary.assertLegalVariableID("Walk", null, FormatUtilities.NUMBER_MANAGER));
		//Just to check
		try
		{
			assertFalse(variableLibrary.isLegalVariableID(globalScope, null));
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok too
		}
		assertFalse(variableLibrary.isLegalVariableID(globalScope, ""));
		assertFalse(variableLibrary.isLegalVariableID(globalScope, " Walk"));
		assertFalse(variableLibrary.isLegalVariableID(globalScope, "Walk "));
	}

	@Test
	public void testAssertVariable()
	{
		SimpleLegalScope globalScope = new SimpleLegalScope("Global");
		LegalScope spScope = new SimpleLegalScope(globalScope, "Spell");
		SimpleLegalScope eqScope = new SimpleLegalScope(globalScope, "Equipment");
		LegalScope eqPartScope = new SimpleLegalScope(eqScope, "Part");
		legalScopeManager.registerScope(globalScope);
		legalScopeManager.registerScope(spScope);
		legalScopeManager.registerScope(eqScope);
		legalScopeManager.registerScope(eqPartScope);
		variableLibrary.assertLegalVariableID("Walk", globalScope, FormatUtilities.NUMBER_MANAGER);
		//Dupe is safe
		variableLibrary.assertLegalVariableID("Walk", globalScope, FormatUtilities.NUMBER_MANAGER);
		assertThrows(LegalVariableException.class, () -> variableLibrary.assertLegalVariableID("Walk", globalScope, FormatUtilities.BOOLEAN_MANAGER));
		assertThrows(LegalVariableException.class, () -> variableLibrary.assertLegalVariableID("Walk", eqScope, FormatUtilities.NUMBER_MANAGER));
		//Check child recursive
		assertThrows(LegalVariableException.class, () -> variableLibrary.assertLegalVariableID("Walk", eqPartScope, FormatUtilities.NUMBER_MANAGER));
		variableLibrary.assertLegalVariableID("Float", eqScope, FormatUtilities.NUMBER_MANAGER);
		assertThrows(LegalVariableException.class, () -> variableLibrary.assertLegalVariableID("Float", eqPartScope, FormatUtilities.NUMBER_MANAGER));
		assertThrows(LegalVariableException.class, () -> variableLibrary.assertLegalVariableID("Float", globalScope, FormatUtilities.NUMBER_MANAGER));
		//Allow peer
		variableLibrary.assertLegalVariableID("Float", spScope, FormatUtilities.NUMBER_MANAGER);
		variableLibrary.assertLegalVariableID("Hover", eqPartScope, FormatUtilities.NUMBER_MANAGER);
		assertThrows(LegalVariableException.class, () -> variableLibrary.assertLegalVariableID("Hover", eqScope, FormatUtilities.NUMBER_MANAGER));
		assertThrows(LegalVariableException.class, () -> variableLibrary.assertLegalVariableID("Hover", globalScope, FormatUtilities.NUMBER_MANAGER));
		variableLibrary.assertLegalVariableID("Drive", spScope, FormatUtilities.NUMBER_MANAGER);
		//Check peer child
		variableLibrary.assertLegalVariableID("Drive", eqPartScope, FormatUtilities.NUMBER_MANAGER);
		variableLibrary.assertLegalVariableID("Fly", spScope, FormatUtilities.NUMBER_MANAGER);
		//Check peer with children
		variableLibrary.assertLegalVariableID("Fly", eqScope, FormatUtilities.NUMBER_MANAGER);
	}

	@Test
	public void testIsLegalVIDFail()
	{
		LegalScope globalScope = new SimpleLegalScope("Global");
		legalScopeManager.registerScope(globalScope);
		variableLibrary.assertLegalVariableID("Walk", globalScope, FormatUtilities.NUMBER_MANAGER);
		assertThrows(NullPointerException.class, () -> variableLibrary.isLegalVariableID(null, "Walk"));
		try
		{
			assertFalse(variableLibrary.isLegalVariableID(globalScope, null));
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
	}

	@Test
	public void testIsLegalVID()
	{
		SimpleLegalScope globalScope = new SimpleLegalScope("Global");
		LegalScope spScope = new SimpleLegalScope(globalScope, "Spell");
		SimpleLegalScope eqScope = new SimpleLegalScope(globalScope, "Equipment");
		LegalScope eqPartScope = new SimpleLegalScope(eqScope, "Part");
		legalScopeManager.registerScope(globalScope);
		legalScopeManager.registerScope(spScope);
		legalScopeManager.registerScope(eqScope);
		legalScopeManager.registerScope(eqPartScope);
		variableLibrary.assertLegalVariableID("Walk", globalScope, FormatUtilities.NUMBER_MANAGER);
		assertTrue(variableLibrary.isLegalVariableID(globalScope, "Walk"));
		assertFalse(variableLibrary.isLegalVariableID(globalScope, "Run"));
		//Works for child
		assertTrue(variableLibrary.isLegalVariableID(eqScope, "Walk"));
		//Works for child recursively
		assertTrue(variableLibrary.isLegalVariableID(eqPartScope, "Walk"));

		variableLibrary.assertLegalVariableID("Float", eqScope, FormatUtilities.NUMBER_MANAGER);
		assertTrue(variableLibrary.isLegalVariableID(eqScope, "Float"));
		//Works for child 
		assertTrue(variableLibrary.isLegalVariableID(eqPartScope, "Float"));
		//but not parent
		assertFalse(variableLibrary.isLegalVariableID(globalScope, "Float"));
		//and not peer
		assertFalse(variableLibrary.isLegalVariableID(spScope, "Float"));

		variableLibrary.assertLegalVariableID("Hover", eqPartScope, FormatUtilities.NUMBER_MANAGER);
		assertTrue(variableLibrary.isLegalVariableID(eqPartScope, "Hover"));
		//but not parent
		assertFalse(variableLibrary.isLegalVariableID(eqScope, "Hover"));
		//or parent recursively
		assertFalse(variableLibrary.isLegalVariableID(globalScope, "Hover"));
		//and not unrelated
		assertFalse(variableLibrary.isLegalVariableID(spScope, "Hover"));
	}

	@Test
	public void testGetVIDFail()
	{
		LegalScope globalScope = new SimpleLegalScope("Global");
		legalScopeManager.registerScope(globalScope);
		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		LegalScope eqScope = new SimpleLegalScope(globalScope, "Equipment");
		legalScopeManager.registerScope(eqScope);
		LegalScope spScope = new SimpleLegalScope(globalScope, "Spell");
		legalScopeManager.registerScope(spScope);
		SimpleVarScoped eq = new SimpleVarScoped();
		eq.scopeName = "Global.Equipment";
		eq.name = "Sword";
		ScopeInstance eqInst = instanceFactory.get("Global.Equipment", Optional.of(eq));
		assertThrows(NullPointerException.class, () -> variableLibrary.getVariableID(null, "Walk"));
		assertThrows(NullPointerException.class, () -> variableLibrary.getVariableID(globalInst, null));
		assertThrows(IllegalArgumentException.class, () -> variableLibrary.getVariableID(globalInst, ""));
		assertThrows(IllegalArgumentException.class, () -> variableLibrary.getVariableID(globalInst, " Walk"));
		assertThrows(IllegalArgumentException.class, () -> variableLibrary.getVariableID(globalInst, "Walk "));
		assertThrows(NoSuchElementException.class, () -> variableLibrary.getVariableID(globalInst, "Walk"));
		assertThrows(NoSuchElementException.class, () -> variableLibrary.getVariableID(eqInst, "Walk"));
		variableLibrary.assertLegalVariableID("Float", spScope, FormatUtilities.NUMBER_MANAGER);
		assertThrows(NoSuchElementException.class, () -> variableLibrary.getVariableID(eqInst, "Float"));
	}

	@Test
	public void testGetVID()
	{
		LegalScope globalScope = new SimpleLegalScope("Global");
		legalScopeManager.registerScope(globalScope);
		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		LegalScope eqScope = new SimpleLegalScope(globalScope, "Equipment");
		legalScopeManager.registerScope(eqScope);
		SimpleVarScoped eq = new SimpleVarScoped();
		eq.scopeName = "Global.Equipment";
		eq.name = "Sword";
		ScopeInstance eqInst = instanceFactory.get("Global.Equipment", Optional.of(eq));
		LegalScope eqPartScope = new SimpleLegalScope(eqScope, "Part");
		legalScopeManager.registerScope(eqPartScope);
		SimpleVarScoped eqpart = new SimpleVarScoped();
		eqpart.scopeName = "Global.Equipment.Part";
		eqpart.name = "Mod";
		eqpart.parent = eq;
		ScopeInstance eqPartInst = instanceFactory.get("Global.Equipment.Part", Optional.of(eqpart));
		variableLibrary.assertLegalVariableID("Walk", globalScope, FormatUtilities.NUMBER_MANAGER);
		variableLibrary.assertLegalVariableID("Float", eqScope, FormatUtilities.NUMBER_MANAGER);
		variableLibrary.assertLegalVariableID("Hover", eqPartScope, FormatUtilities.NUMBER_MANAGER);
		VariableID<?> vid = variableLibrary.getVariableID(globalInst, "Walk");
		assertEquals("Walk", vid.getName());
		assertEquals(globalInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = variableLibrary.getVariableID(eqInst, "Float");
		assertEquals("Float", vid.getName());
		assertEquals(eqInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = variableLibrary.getVariableID(eqInst, "Walk");
		assertEquals("Walk", vid.getName());
		//NOTE: Global scope here even though eqScope was passed into getVariableID
		assertEquals(globalInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = variableLibrary.getVariableID(eqPartInst, "Walk");
		assertEquals("Walk", vid.getName());
		//NOTE: Global scope here even though eqPartScope was passed into getVariableID
		assertEquals(globalInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = variableLibrary.getVariableID(eqPartInst, "Hover");
		assertEquals("Hover", vid.getName());
		assertEquals(eqPartInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = variableLibrary.getVariableID(eqPartInst, "Hover");
		assertEquals("Hover", vid.getName());
		assertEquals(eqPartInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());
	}

	@Test
	public void testGetVariableFormat()
	{
		LegalScope globalScope = new SimpleLegalScope("Global");
		LegalScope eqScope = new SimpleLegalScope(globalScope, "Equipment");
		legalScopeManager.registerScope(globalScope);
		legalScopeManager.registerScope(eqScope);
		variableLibrary.assertLegalVariableID("Walk", globalScope, FormatUtilities.NUMBER_MANAGER);
		variableLibrary.assertLegalVariableID("Float", eqScope, FormatUtilities.NUMBER_MANAGER);
		assertThrows(NullPointerException.class, () -> variableLibrary.getVariableFormat(null, "Walk"));
		try
		{
			Object o = variableLibrary.getVariableFormat(globalScope, null);
			assertTrue(o == null);
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok too
		}
		assertTrue(FormatUtilities.NUMBER_MANAGER
			.equals(variableLibrary.getVariableFormat(globalScope, "Walk")));
		assertTrue(
			FormatUtilities.NUMBER_MANAGER.equals(variableLibrary.getVariableFormat(eqScope, "Float")));
		//fail at depth
		assertTrue(
			FormatUtilities.NUMBER_MANAGER.equals(variableLibrary.getVariableFormat(eqScope, "Walk")));
		//work indirect
		assertTrue(variableLibrary.getVariableFormat(globalScope, "Float") == null);
	}

	@Test
	public void testProveReuse()
	{
		SimpleLegalScope globalScope = new SimpleLegalScope("Global");
		legalScopeManager.registerScope(globalScope);
		LegalScope eqScope =
				new SimpleLegalScope(globalScope, "Equipment");
		legalScopeManager.registerScope(eqScope);
		SimpleVarScoped eq = new SimpleVarScoped();
		eq.scopeName = "Global.Equipment";
		eq.name = "Sword";
		ScopeInstance eqInst = instanceFactory.get("Global.Equipment", Optional.of(eq));
		LegalScope abScope = new SimpleLegalScope(globalScope, "Ability");
		legalScopeManager.registerScope(abScope);
		SimpleVarScoped ab = new SimpleVarScoped();
		ab.scopeName = "Global.Ability";
		ab.name = "Dodge";
		ScopeInstance abInst = instanceFactory.get("Global.Ability", Optional.of(ab));

		variableLibrary.assertLegalVariableID("Walk", eqScope, FormatUtilities.NUMBER_MANAGER);
		VariableID<?> vidm = variableLibrary.getVariableID(eqInst, "Walk");
		assertEquals("Walk", vidm.getName());
		assertEquals(eqInst, vidm.getScope());
		assertEquals(Number.class, vidm.getVariableFormat());

		variableLibrary.assertLegalVariableID("Walk", abScope, FormatUtilities.BOOLEAN_MANAGER);
		VariableID<?> vidf = variableLibrary.getVariableID(abInst, "Walk");
		assertEquals("Walk", vidf.getName());
		assertEquals(abInst, vidf.getScope());
		assertEquals(FormatUtilities.BOOLEAN_CLASS, vidf.getVariableFormat());

		assertFalse(vidm.equals(vidf));
		assertFalse(vidf.equals(vidm));

	}

	@Test
	public void testOrderOfOps()
	{
		SimpleLegalScope globalScope = new SimpleLegalScope("Global");
		legalScopeManager.registerScope(globalScope);
		DeferredIndirect def = new DeferredIndirect();
		valueStore.addValueFor(FormatUtilities.ORDEREDPAIR_MANAGER, def);
		variableLibrary.assertLegalVariableID("Walk", globalScope,
			FormatUtilities.ORDEREDPAIR_MANAGER);
		assertFalse(variableLibrary.getInvalidFormats().isEmpty());
		def.setPair(new OrderedPair(0, 0));
		assertTrue(variableLibrary.getInvalidFormats().isEmpty());
	}

	private static final class DeferredIndirect implements Indirect<OrderedPair>
	{

		private OrderedPair pair;
		
		public void setPair(OrderedPair pair)
		{
			this.pair = pair;
		}

		@Override
		public OrderedPair get()
		{
			return pair;
		}

		@Override
		public String getUnconverted()
		{
			return pair.toString();
		}
		
	}

}

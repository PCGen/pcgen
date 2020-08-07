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

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.exception.LegalVariableException;
import pcgen.base.math.OrderedPair;
import pcgen.base.solver.SupplierValueStore;
import pcgen.base.testsupport.NaiveScopeManager;
import pcgen.base.testsupport.SimpleVarScoped;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;

public class VariableManagerTest
{

	private ScopeInstanceFactory instanceFactory;
	private NaiveScopeManager scopeManager;
	private VariableLibrary variableLibrary;
	private SupplierValueStore valueStore;

	@BeforeEach
	void setUp()
	{
		valueStore = new SupplierValueStore();
		scopeManager = new NaiveScopeManager();
		instanceFactory = new SimpleScopeInstanceFactory(scopeManager);
		valueStore.addSolverFormat(FormatUtilities.NUMBER_MANAGER, () -> 0);
		valueStore.addSolverFormat(FormatUtilities.STRING_MANAGER, () -> "");
		valueStore.addSolverFormat(FormatUtilities.BOOLEAN_MANAGER, () -> false);
		variableLibrary = new VariableManager(scopeManager, scopeManager, instanceFactory, valueStore);
	}
	
	@AfterEach
	void tearDown()
	{
		instanceFactory = null;
		scopeManager = null;
		variableLibrary = null;
		valueStore = null;
	}

	@Test
	public void testNullConstructor()
	{
		assertThrows(NullPointerException.class, () -> new VariableManager(null, scopeManager, instanceFactory, valueStore));
		assertThrows(NullPointerException.class, () -> new VariableManager(scopeManager, null, instanceFactory, valueStore));
		assertThrows(NullPointerException.class, () -> new VariableManager(scopeManager, scopeManager, null, valueStore));
		assertThrows(NullPointerException.class, () -> new VariableManager(scopeManager, scopeManager, instanceFactory, null));
	}

	@Test
	public void testAssertVariableFail()
	{
		ImplementedScope globalImplementedScope = getGlobalImpl();
		assertThrows(NullPointerException.class, () -> variableLibrary.assertLegalVariableID(null, globalImplementedScope, FormatUtilities.NUMBER_MANAGER));
		assertThrows(IllegalArgumentException.class, () -> variableLibrary.assertLegalVariableID("", globalImplementedScope, FormatUtilities.NUMBER_MANAGER));
		assertThrows(IllegalArgumentException.class, () -> variableLibrary.assertLegalVariableID(" Walk", globalImplementedScope, FormatUtilities.NUMBER_MANAGER));
		assertThrows(IllegalArgumentException.class, () -> variableLibrary.assertLegalVariableID("Walk ", globalImplementedScope, FormatUtilities.NUMBER_MANAGER));
		assertThrows(NullPointerException.class, () -> variableLibrary.assertLegalVariableID("Walk", globalImplementedScope, null));
		assertThrows(NullPointerException.class, () -> variableLibrary.assertLegalVariableID("Walk", null, FormatUtilities.NUMBER_MANAGER));
		//Just to check
		try
		{
			assertFalse(variableLibrary.isLegalVariableID(globalImplementedScope, null));
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok too
		}
		assertFalse(variableLibrary.isLegalVariableID(globalImplementedScope, ""));
		assertFalse(variableLibrary.isLegalVariableID(globalImplementedScope, " Walk"));
		assertFalse(variableLibrary.isLegalVariableID(globalImplementedScope, "Walk "));
	}

	@Test
	public void testAssertVariable()
	{
		scopeManager.registerScope("Global", "Equipment");
		scopeManager.registerScope("Equipment", "Part");
		ImplementedScope globalImplementedScope = scopeManager.getImplementedScope("Global");
		ImplementedScope equipmentImplementedScope = scopeManager.getImplementedScope("Global.Equipment");
		ImplementedScope partImplementedScope = scopeManager.getImplementedScope("Global.Equipment.Part");
		ImplementedScope spellImplementedScope = getSubScope("Global", "Spell");
		variableLibrary.assertLegalVariableID("Walk", globalImplementedScope, FormatUtilities.NUMBER_MANAGER);
		//Dupe is safe
		variableLibrary.assertLegalVariableID("Walk", globalImplementedScope, FormatUtilities.NUMBER_MANAGER);
		assertThrows(LegalVariableException.class, () -> variableLibrary.assertLegalVariableID("Walk", globalImplementedScope, FormatUtilities.BOOLEAN_MANAGER));
		assertThrows(LegalVariableException.class, () -> variableLibrary.assertLegalVariableID("Walk", equipmentImplementedScope, FormatUtilities.NUMBER_MANAGER));
		//Check child recursive
		assertThrows(LegalVariableException.class, () -> variableLibrary.assertLegalVariableID("Walk", partImplementedScope, FormatUtilities.NUMBER_MANAGER));
		variableLibrary.assertLegalVariableID("Float", equipmentImplementedScope, FormatUtilities.NUMBER_MANAGER);
		assertThrows(LegalVariableException.class, () -> variableLibrary.assertLegalVariableID("Float", partImplementedScope, FormatUtilities.NUMBER_MANAGER));
		assertThrows(LegalVariableException.class, () -> variableLibrary.assertLegalVariableID("Float", globalImplementedScope, FormatUtilities.NUMBER_MANAGER));
		//Allow peer
		variableLibrary.assertLegalVariableID("Float", spellImplementedScope, FormatUtilities.NUMBER_MANAGER);
		variableLibrary.assertLegalVariableID("Hover", partImplementedScope, FormatUtilities.NUMBER_MANAGER);
		assertThrows(LegalVariableException.class, () -> variableLibrary.assertLegalVariableID("Hover", equipmentImplementedScope, FormatUtilities.NUMBER_MANAGER));
		assertThrows(LegalVariableException.class, () -> variableLibrary.assertLegalVariableID("Hover", globalImplementedScope, FormatUtilities.NUMBER_MANAGER));
		variableLibrary.assertLegalVariableID("Drive", spellImplementedScope, FormatUtilities.NUMBER_MANAGER);
		//Check peer child
		variableLibrary.assertLegalVariableID("Drive", partImplementedScope, FormatUtilities.NUMBER_MANAGER);
		variableLibrary.assertLegalVariableID("Fly", spellImplementedScope, FormatUtilities.NUMBER_MANAGER);
		//Check peer with children
		variableLibrary.assertLegalVariableID("Fly", equipmentImplementedScope, FormatUtilities.NUMBER_MANAGER);
	}

	@Test
	public void testIsLegalVIDFail()
	{
		ImplementedScope globalImplementedScope = getGlobalImpl();

		variableLibrary.assertLegalVariableID("Walk", globalImplementedScope, FormatUtilities.NUMBER_MANAGER);
		assertThrows(NullPointerException.class, () -> variableLibrary.isLegalVariableID(null, "Walk"));
		try
		{
			assertFalse(variableLibrary.isLegalVariableID(globalImplementedScope, null));
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
	}

	@Test
	public void testIsLegalVID()
	{
		ImplementedScope globalImplementedScope = scopeManager.getImplementedScope("Global");

		ImplementedScope spellImplementedScope = getSubScope("Global", "Spell");

		scopeManager.registerScope("Global", "Equipment");
		ImplementedScope equipmentImplementedScope = scopeManager.getImplementedScope("Global.Equipment");

		scopeManager.registerScope("Equipment", "Part");
		ImplementedScope partImplementedScope = scopeManager.getImplementedScope("Global.Equipment.Part");

		variableLibrary.assertLegalVariableID("Walk", globalImplementedScope, FormatUtilities.NUMBER_MANAGER);
		assertTrue(variableLibrary.isLegalVariableID(globalImplementedScope, "Walk"));
		assertFalse(variableLibrary.isLegalVariableID(globalImplementedScope, "Run"));
		//Works for child
		assertTrue(variableLibrary.isLegalVariableID(equipmentImplementedScope, "Walk"));
		//Works for child recursively
		assertTrue(variableLibrary.isLegalVariableID(partImplementedScope, "Walk"));

		variableLibrary.assertLegalVariableID("Float", equipmentImplementedScope, FormatUtilities.NUMBER_MANAGER);
		assertTrue(variableLibrary.isLegalVariableID(equipmentImplementedScope, "Float"));
		//Works for child 
		assertTrue(variableLibrary.isLegalVariableID(partImplementedScope, "Float"));
		//but not parent
		assertFalse(variableLibrary.isLegalVariableID(globalImplementedScope, "Float"));
		//and not peer
		assertFalse(variableLibrary.isLegalVariableID(spellImplementedScope, "Float"));

		variableLibrary.assertLegalVariableID("Hover", partImplementedScope, FormatUtilities.NUMBER_MANAGER);
		assertTrue(variableLibrary.isLegalVariableID(partImplementedScope, "Hover"));
		//but not parent
		assertFalse(variableLibrary.isLegalVariableID(equipmentImplementedScope, "Hover"));
		//or parent recursively
		assertFalse(variableLibrary.isLegalVariableID(globalImplementedScope, "Hover"));
		//and not unrelated
		assertFalse(variableLibrary.isLegalVariableID(spellImplementedScope, "Hover"));
	}

	@Test
	public void testGetVIDFail()
	{
		scopeManager.registerScope("Global", "Spell");
		ImplementedScope spellImplementedScope = scopeManager.getImplementedScope("Global.Spell");

		scopeManager.registerScope("Global", "Equipment");
		scopeManager.registerScope("Equipment", "Part");

		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		SimpleVarScoped eq = new SimpleVarScoped();
		eq.scopeName = "Global.Equipment";
		eq.name = "Sword";
		ScopeInstance eqInst = instanceFactory.get("Global.Equipment", Optional.of(eq));
		assertThrows(NullPointerException.class, () -> variableLibrary.getVariableID(null, "Walk"));
		assertThrows(NullPointerException.class, () -> variableLibrary.getVariableID(globalInst, null));
		assertThrows(IllegalArgumentException.class, () -> variableLibrary.getVariableID(globalInst, ""));
		assertThrows(IllegalArgumentException.class, () -> variableLibrary.getVariableID(globalInst, " Walk"));
		assertThrows(IllegalArgumentException.class, () -> variableLibrary.getVariableID(globalInst, "Walk "));
		assertThrows(IllegalArgumentException.class, () -> variableLibrary.getVariableID(globalInst, "Walk"));
		assertThrows(IllegalArgumentException.class, () -> variableLibrary.getVariableID(eqInst, "Walk"));
		variableLibrary.assertLegalVariableID("Float", spellImplementedScope, FormatUtilities.NUMBER_MANAGER);
		assertThrows(IllegalArgumentException.class, () -> variableLibrary.getVariableID(eqInst, "Float"));
	}

	@Test
	public void testGetVID()
	{
		ImplementedScope globalImplementedScope = scopeManager.getImplementedScope("Global");

		scopeManager.registerScope("Global", "Equipment");
		ImplementedScope equipmentImplementedScope = scopeManager.getImplementedScope("Global.Equipment");

		scopeManager.registerScope("Equipment", "Part");
		ImplementedScope partImplementedScope = scopeManager.getImplementedScope("Global.Equipment.Part");

		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		SimpleVarScoped eq = new SimpleVarScoped();
		eq.scopeName = "Global.Equipment";
		eq.name = "Sword";
		ScopeInstance eqInst = instanceFactory.get("Global.Equipment", Optional.of(eq));
		SimpleVarScoped eqpart = new SimpleVarScoped();
		eqpart.scopeName = "Global.Equipment.Part";
		eqpart.name = "Mod";
		eqpart.parent = eq;
		ScopeInstance eqPartInst = instanceFactory.get("Global.Equipment.Part", Optional.of(eqpart));
		variableLibrary.assertLegalVariableID("Walk", globalImplementedScope, FormatUtilities.NUMBER_MANAGER);
		variableLibrary.assertLegalVariableID("Float", equipmentImplementedScope, FormatUtilities.NUMBER_MANAGER);
		variableLibrary.assertLegalVariableID("Hover", partImplementedScope, FormatUtilities.NUMBER_MANAGER);
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
		ImplementedScope globalImplementedScope = scopeManager.getImplementedScope("Global");

		ImplementedScope equipmentImplementedScope = getSubScope("Global", "Equipment");

		variableLibrary.assertLegalVariableID("Walk", globalImplementedScope, FormatUtilities.NUMBER_MANAGER);
		variableLibrary.assertLegalVariableID("Float", equipmentImplementedScope, FormatUtilities.NUMBER_MANAGER);
		assertThrows(NullPointerException.class, () -> variableLibrary.getVariableFormat(null, "Walk"));
		try
		{
			Optional<FormatManager<?>> o = variableLibrary.getVariableFormat(globalImplementedScope, null);
			assertTrue(o.isEmpty());
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok too
		}
		//Works for variables at same scope
		assertTrue(
			FormatUtilities.NUMBER_MANAGER.equals(variableLibrary.getVariableFormat(equipmentImplementedScope, "Float").get()));
		assertTrue(
			FormatUtilities.NUMBER_MANAGER.equals(variableLibrary.getVariableFormat(globalImplementedScope, "Walk").get()));
		//Works for variable from higher scope
		assertTrue(
			FormatUtilities.NUMBER_MANAGER.equals(variableLibrary.getVariableFormat(equipmentImplementedScope, "Walk").get()));
		//Fail for variable from lower scope
		assertTrue(variableLibrary.getVariableFormat(globalImplementedScope, "Float").isEmpty());
	}

	@Test
	public void testProveReuse()
	{
		ImplementedScope equipmentImplementedScope = getSubScope("Global", "Equipment");
		ImplementedScope abilityImplementedScope = getSubScope("Global", "Ability");

		SimpleVarScoped eq = new SimpleVarScoped();
		eq.scopeName = "Global.Equipment";
		eq.name = "Sword";
		ScopeInstance eqInst = instanceFactory.get("Global.Equipment", Optional.of(eq));
		SimpleVarScoped ab = new SimpleVarScoped();
		ab.scopeName = "Global.Ability";
		ab.name = "Dodge";
		ScopeInstance abInst = instanceFactory.get("Global.Ability", Optional.of(ab));

		variableLibrary.assertLegalVariableID("Walk", equipmentImplementedScope, FormatUtilities.NUMBER_MANAGER);
		VariableID<?> vidm = variableLibrary.getVariableID(eqInst, "Walk");
		assertEquals("Walk", vidm.getName());
		assertEquals(eqInst, vidm.getScope());
		assertEquals(Number.class, vidm.getVariableFormat());

		variableLibrary.assertLegalVariableID("Walk", abilityImplementedScope, FormatUtilities.BOOLEAN_MANAGER);
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
		ImplementedScope globalImplementedScope = getGlobalImpl();

		DeferredIndirect def = new DeferredIndirect();
		valueStore.addSolverFormat(FormatUtilities.ORDEREDPAIR_MANAGER, def);
		variableLibrary.assertLegalVariableID("Walk", globalImplementedScope,
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

	private ImplementedScope getGlobalImpl()
	{
		return scopeManager.getImplementedScope("Global");
	}

	private ImplementedScope getSubScope(String parent, String name)
	{
		scopeManager.registerScope(parent, name);
		return scopeManager.getImplementedScope(parent + "." + name);
	}

}

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

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Test;

import junit.framework.TestCase;
import pcgen.base.format.BooleanManager;
import pcgen.base.format.NumberManager;
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

public class VariableManagerTest extends TestCase
{

	private NumberManager numberManager = FormatUtilities.NUMBER_MANAGER;
	private BooleanManager booleanManager = FormatUtilities.BOOLEAN_MANAGER;
	private ScopeInstanceFactory instanceFactory;
	private ScopeManagerInst legalScopeManager;
	private VariableLibrary variableLibrary;
	private SupplierValueStore valueStore;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		valueStore = new SupplierValueStore();
		legalScopeManager = new ScopeManagerInst();
		instanceFactory = new SimpleScopeInstanceFactory(legalScopeManager);
		valueStore.addValueFor(FormatUtilities.NUMBER_MANAGER, () -> 0);
		valueStore.addValueFor(FormatUtilities.STRING_MANAGER, () -> "");
		valueStore.addValueFor(FormatUtilities.BOOLEAN_MANAGER, () -> false);
		variableLibrary = new VariableManager(legalScopeManager, valueStore);
	}

	@SuppressWarnings("unused")
	@Test
	public void testNullConstructor()
	{
		try
		{
			new VariableManager(null, valueStore);
			fail("null must be rejected in constructor");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			new VariableManager(legalScopeManager, null);
			fail("null must be rejected in constructor");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			new VariableManager(null, null);
			fail("null must be rejected in constructor");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
	}

	@Test
	public void testAssertVariableFail()
	{
		LegalScope globalScope = new SimpleLegalScope("Global");
		try
		{
			variableLibrary.assertLegalVariableID(null, globalScope, numberManager);
			fail("null var must be rejected");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			variableLibrary.assertLegalVariableID("", globalScope, numberManager);
			fail("empty var must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			variableLibrary.assertLegalVariableID(" Walk", globalScope, numberManager);
			fail("padded var must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			variableLibrary.assertLegalVariableID("Walk ", globalScope, numberManager);
			fail("padded var must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			variableLibrary.assertLegalVariableID("Walk", globalScope, null);
			fail("null FormatManager must be rejected");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			variableLibrary.assertLegalVariableID("Walk", null, numberManager);
			fail("null scope must be rejected");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		//Just to check
		try
		{
			assertFalse(variableLibrary.isLegalVariableID(globalScope, null));
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
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
		variableLibrary.assertLegalVariableID("Walk", globalScope, numberManager);
		//Dupe is safe
		variableLibrary.assertLegalVariableID("Walk", globalScope, numberManager);
		try
		{
			variableLibrary.assertLegalVariableID("Walk", globalScope, booleanManager);
			fail("different format should fail");
		}
		catch (LegalVariableException e)
		{
			//expected
		}
		try
		{
			variableLibrary.assertLegalVariableID("Walk", eqScope, numberManager);
			fail("child scope of existing should fail");
		}
		catch (LegalVariableException e)
		{
			//expected
		}
		//Check child recursive
		try
		{
			variableLibrary.assertLegalVariableID("Walk", eqPartScope, numberManager);
			fail("child scope (recursive) of existing should fail");
		}
		catch (LegalVariableException e)
		{
			//expected
		}
		variableLibrary.assertLegalVariableID("Float", eqScope, numberManager);
		try
		{
			variableLibrary.assertLegalVariableID("Float", eqPartScope, numberManager);
			fail("child scope of existing should fail");
		}
		catch (LegalVariableException e)
		{
			//expected
		}
		try
		{
			variableLibrary.assertLegalVariableID("Float", globalScope, numberManager);
			fail("parent scope of existing should fail");
		}
		catch (LegalVariableException e)
		{
			//expected
		}
		//Allow peer
		variableLibrary.assertLegalVariableID("Float", spScope, numberManager);
		variableLibrary.assertLegalVariableID("Hover", eqPartScope, numberManager);
		try
		{
			variableLibrary.assertLegalVariableID("Hover", eqScope, numberManager);
			fail("parent scope of existing should fail");
		}
		catch (LegalVariableException e)
		{
			//expected
		}
		try
		{
			variableLibrary.assertLegalVariableID("Hover", globalScope, numberManager);
			fail("parent scope (recursive) of existing should fail");
		}
		catch (LegalVariableException e)
		{
			//expected
		}
		variableLibrary.assertLegalVariableID("Drive", spScope, numberManager);
		//Check peer child
		variableLibrary.assertLegalVariableID("Drive", eqPartScope, numberManager);
		variableLibrary.assertLegalVariableID("Fly", spScope, numberManager);
		//Check peer with children
		variableLibrary.assertLegalVariableID("Fly", eqScope, numberManager);
	}

	@Test
	public void testIsLegalVIDFail()
	{
		LegalScope globalScope = new SimpleLegalScope("Global");
		legalScopeManager.registerScope(globalScope);
		variableLibrary.assertLegalVariableID("Walk", globalScope, numberManager);
		try
		{
			variableLibrary.isLegalVariableID(null, "Walk");
			fail("null FormatManager must be rejected");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
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
		variableLibrary.assertLegalVariableID("Walk", globalScope, numberManager);
		assertTrue(variableLibrary.isLegalVariableID(globalScope, "Walk"));
		assertFalse(variableLibrary.isLegalVariableID(globalScope, "Run"));
		//Works for child
		assertTrue(variableLibrary.isLegalVariableID(eqScope, "Walk"));
		//Works for child recursively
		assertTrue(variableLibrary.isLegalVariableID(eqPartScope, "Walk"));

		variableLibrary.assertLegalVariableID("Float", eqScope, numberManager);
		assertTrue(variableLibrary.isLegalVariableID(eqScope, "Float"));
		//Works for child 
		assertTrue(variableLibrary.isLegalVariableID(eqPartScope, "Float"));
		//but not parent
		assertFalse(variableLibrary.isLegalVariableID(globalScope, "Float"));
		//and not peer
		assertFalse(variableLibrary.isLegalVariableID(spScope, "Float"));

		variableLibrary.assertLegalVariableID("Hover", eqPartScope, numberManager);
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
		try
		{
			variableLibrary.getVariableID(null, "Walk");
			fail("null scope must be rejected");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			variableLibrary.getVariableID(globalInst, null);
			fail("null name must be rejected");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			variableLibrary.getVariableID(globalInst, "");
			fail("empty name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			variableLibrary.getVariableID(globalInst, " Walk");
			fail("padded name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			variableLibrary.getVariableID(globalInst, "Walk ");
			fail("padded name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			variableLibrary.getVariableID(globalInst, "Walk");
			fail("undefined name must be rejected");
		}
		catch (NoSuchElementException | IllegalArgumentException | NullPointerException e)
		{
			//undefined, ok
		}
		try
		{
			variableLibrary.getVariableID(eqInst, "Walk");
			fail("undefined name must be rejected");
		}
		catch (NoSuchElementException | IllegalArgumentException | NullPointerException e)
		{
			//undefined, ok
		}
		variableLibrary.assertLegalVariableID("Float", spScope, numberManager);
		try
		{
			variableLibrary.getVariableID(eqInst, "Float");
			fail("undefined name (unrelated scope) must be rejected");
		}
		catch (NoSuchElementException | IllegalArgumentException | NullPointerException e)
		{
			//undefined, ok
		}

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
		variableLibrary.assertLegalVariableID("Walk", globalScope, numberManager);
		variableLibrary.assertLegalVariableID("Float", eqScope, numberManager);
		variableLibrary.assertLegalVariableID("Hover", eqPartScope, numberManager);
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
		variableLibrary.assertLegalVariableID("Walk", globalScope, numberManager);
		variableLibrary.assertLegalVariableID("Float", eqScope, numberManager);
		try
		{
			variableLibrary.getVariableFormat(null, "Walk");
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			Object o = variableLibrary.getVariableFormat(globalScope, null);
			assertTrue(o == null);
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok too
		}
		assertTrue(numberManager
			.equals(variableLibrary.getVariableFormat(globalScope, "Walk")));
		assertTrue(
			numberManager.equals(variableLibrary.getVariableFormat(eqScope, "Float")));
		//fail at depth
		assertTrue(
			numberManager.equals(variableLibrary.getVariableFormat(eqScope, "Walk")));
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

		variableLibrary.assertLegalVariableID("Walk", eqScope, numberManager);
		VariableID<?> vidm = variableLibrary.getVariableID(eqInst, "Walk");
		assertEquals("Walk", vidm.getName());
		assertEquals(eqInst, vidm.getScope());
		assertEquals(Number.class, vidm.getVariableFormat());

		variableLibrary.assertLegalVariableID("Walk", abScope, booleanManager);
		VariableID<?> vidf = variableLibrary.getVariableID(abInst, "Walk");
		assertEquals("Walk", vidf.getName());
		assertEquals(abInst, vidf.getScope());
		assertEquals(Boolean.class, vidf.getVariableFormat());

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

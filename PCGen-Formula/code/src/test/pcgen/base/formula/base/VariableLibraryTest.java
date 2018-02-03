/*
 * pyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.base;

import java.util.Set;

import pcgen.base.format.BooleanManager;
import pcgen.base.format.NumberManager;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.inst.ScopeInstanceFactory;
import pcgen.base.formula.inst.SimpleLegalScope;
import pcgen.base.testsupport.SimpleVarScoped;

import junit.framework.TestCase;
import org.junit.Test;

public class VariableLibraryTest extends TestCase
{

	private NumberManager numberManager = FormatUtilities.NUMBER_MANAGER;
	private ScopeInstanceFactory instanceFactory;
	private LegalScopeLibrary varScopeLib;
	private VariableLibrary varLib;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		varScopeLib = new LegalScopeLibrary();
		instanceFactory = new ScopeInstanceFactory(varScopeLib);
		varLib = new VariableLibrary(varScopeLib);
	}

	@Test
	public void testNullConstructor()
	{
		try
		{
			new VariableLibrary(null);
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
		LegalScope globalScope = new SimpleLegalScope(null, "Global");
		try
		{
			varLib.assertLegalVariableID(null, globalScope, numberManager);
			fail("null var must be rejected");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			varLib.assertLegalVariableID("", globalScope, numberManager);
			fail("empty var must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.assertLegalVariableID(" Walk", globalScope, numberManager);
			fail("padded var must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.assertLegalVariableID("Walk ", globalScope, numberManager);
			fail("padded var must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.assertLegalVariableID("Walk", globalScope, null);
			fail("null FormatManager must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.assertLegalVariableID("Walk", null, numberManager);
			fail("null scope must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		//Just to check
		try
		{
			assertFalse(varLib.isLegalVariableID(globalScope, null));
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		assertFalse(varLib.isLegalVariableID(globalScope, ""));
		assertFalse(varLib.isLegalVariableID(globalScope, " Walk"));
		assertFalse(varLib.isLegalVariableID(globalScope, "Walk "));
	}

	@Test
	public void testAssertVariable()
	{
		SimpleLegalScope globalScope = new SimpleLegalScope(null, "Global");
		LegalScope spScope = new SimpleLegalScope(globalScope, "Spell");
		SimpleLegalScope eqScope =
				new SimpleLegalScope(globalScope, "Equipment");
		LegalScope eqPartScope = new SimpleLegalScope(eqScope, "Part");
		assertTrue(
			varLib.assertLegalVariableID("Walk", globalScope, numberManager));
		//Dupe is safe
		assertTrue(
			varLib.assertLegalVariableID("Walk", globalScope, numberManager));
		//Check child
		assertFalse(
			varLib.assertLegalVariableID("Walk", eqScope, numberManager));
		//Check child recursive
		assertFalse(
			varLib.assertLegalVariableID("Walk", eqPartScope, numberManager));

		assertTrue(
			varLib.assertLegalVariableID("Float", eqScope, numberManager));
		//Check child
		assertFalse(
			varLib.assertLegalVariableID("Float", eqPartScope, numberManager));
		//Check parent
		assertFalse(
			varLib.assertLegalVariableID("Float", globalScope, numberManager));
		//Allow peer
		assertTrue(
			varLib.assertLegalVariableID("Float", spScope, numberManager));

		assertTrue(
			varLib.assertLegalVariableID("Hover", eqPartScope, numberManager));
		//Check parent
		assertFalse(
			varLib.assertLegalVariableID("Hover", eqScope, numberManager));
		//Check parent recursive
		assertFalse(
			varLib.assertLegalVariableID("Hover", globalScope, numberManager));

		assertTrue(
			varLib.assertLegalVariableID("Drive", spScope, numberManager));
		//Check peer child
		assertTrue(
			varLib.assertLegalVariableID("Drive", eqPartScope, numberManager));

		assertTrue(varLib.assertLegalVariableID("Fly", spScope, numberManager));
		//Check peer with children
		assertTrue(varLib.assertLegalVariableID("Fly", eqScope, numberManager));

	}

	@Test
	public void testIsLegalVIDFail()
	{
		LegalScope globalScope = new SimpleLegalScope(null, "Global");
		assertTrue(
			varLib.assertLegalVariableID("Walk", globalScope, numberManager));
		try
		{
			varLib.isLegalVariableID(null, "Walk");
			fail("null FormatManager must be rejected");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			assertFalse(varLib.isLegalVariableID(globalScope, null));
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
	}

	@Test
	public void testIsLegalVID()
	{
		SimpleLegalScope globalScope = new SimpleLegalScope(null, "Global");
		LegalScope spScope = new SimpleLegalScope(globalScope, "Spell");
		SimpleLegalScope eqScope =
				new SimpleLegalScope(globalScope, "Equipment");
		LegalScope eqPartScope = new SimpleLegalScope(eqScope, "Part");
		assertTrue(
			varLib.assertLegalVariableID("Walk", globalScope, numberManager));
		assertTrue(varLib.isLegalVariableID(globalScope, "Walk"));
		assertFalse(varLib.isLegalVariableID(globalScope, "Run"));
		//Works for child
		assertTrue(varLib.isLegalVariableID(eqScope, "Walk"));
		//Works for child recursively
		assertTrue(varLib.isLegalVariableID(eqPartScope, "Walk"));

		assertTrue(
			varLib.assertLegalVariableID("Float", eqScope, numberManager));
		assertTrue(varLib.isLegalVariableID(eqScope, "Float"));
		//Works for child 
		assertTrue(varLib.isLegalVariableID(eqPartScope, "Float"));
		//but not parent
		assertFalse(varLib.isLegalVariableID(globalScope, "Float"));
		//and not peer
		assertFalse(varLib.isLegalVariableID(spScope, "Float"));

		assertTrue(
			varLib.assertLegalVariableID("Hover", eqPartScope, numberManager));
		assertTrue(varLib.isLegalVariableID(eqPartScope, "Hover"));
		//but not parent
		assertFalse(varLib.isLegalVariableID(eqScope, "Hover"));
		//or parent recursively
		assertFalse(varLib.isLegalVariableID(globalScope, "Hover"));
		//and not unrelated
		assertFalse(varLib.isLegalVariableID(spScope, "Hover"));
	}

	@Test
	public void testKnownVarScopeFail()
	{
		LegalScope globalScope = new SimpleLegalScope(null, "Global");
		assertTrue(
			varLib.assertLegalVariableID("Walk", globalScope, numberManager));
		try
		{
			varLib.getKnownLegalScopes(null);
			fail("null name must be rejected");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			varLib.getKnownLegalScopes("");
			fail("empty name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.getKnownLegalScopes("Walk ");
			fail("padded name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.getKnownLegalScopes(" Walk");
			fail("padded name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testKnownVarScope()
	{
		LegalScope globalScope = new SimpleLegalScope(null, "Global");
		LegalScope spScope = new SimpleLegalScope(globalScope, "Spell");
		LegalScope eqScope = new SimpleLegalScope(globalScope, "Equipment");
		LegalScope eqPartScope = new SimpleLegalScope(eqScope, "Part");
		assertTrue(
			varLib.assertLegalVariableID("Walk", globalScope, numberManager));
		assertTrue(
			varLib.assertLegalVariableID("Float", eqScope, numberManager));
		assertTrue(
			varLib.assertLegalVariableID("Hover", eqPartScope, numberManager));
		assertTrue(
			varLib.assertLegalVariableID("Hover", spScope, numberManager));
		Set<LegalScope> list = varLib.getKnownLegalScopes("Walk");
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(globalScope, list.iterator().next());
		//Assert independence (would be a conflict)
		try
		{
			list.add(spScope);
			assertFalse(varLib.getKnownLegalScopes("Walk").contains(spScope));
		}
		catch (UnsupportedOperationException e)
		{
			//also ok if list was unwriteable
		}

		list = varLib.getKnownLegalScopes("Float");
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(eqScope, list.iterator().next());
		//Assert independence (no conflict)
		try
		{
			list.add(spScope);
			assertFalse(varLib.getKnownLegalScopes("Float").contains(spScope));
		}
		catch (UnsupportedOperationException e)
		{
			//also ok if list was unwriteable
		}

		list = varLib.getKnownLegalScopes("Hover");
		assertNotNull(list);
		assertEquals(2, list.size());
		assertTrue(list.contains(spScope));
		assertTrue(list.contains(eqPartScope));
	}

	@Test
	public void testGetVIDFail()
	{
		LegalScope globalScope = new SimpleLegalScope(null, "Global");
		varScopeLib.registerScope(globalScope);
		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		LegalScope eqScope = new SimpleLegalScope(globalScope, "Equipment");
		varScopeLib.registerScope(eqScope);
		LegalScope spScope = new SimpleLegalScope(globalScope, "Spell");
		varScopeLib.registerScope(spScope);
		SimpleVarScoped eq = new SimpleVarScoped();
		eq.scopeName = "Global.Equipment";
		eq.name = "Sword";
		ScopeInstance eqInst = instanceFactory.get("Global.Equipment", eq);
		try
		{
			varLib.getVariableID(null, "Walk");
			fail("null scope must be rejected");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			varLib.getVariableID(globalInst, null);
			fail("null name must be rejected");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			varLib.getVariableID(globalInst, "");
			fail("empty name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.getVariableID(globalInst, " Walk");
			fail("padded name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.getVariableID(globalInst, "Walk ");
			fail("padded name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.getVariableID(globalInst, "Walk");
			fail("undefined name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//undefined, ok
		}
		try
		{
			varLib.getVariableID(eqInst, "Walk");
			fail("undefined name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//undefined, ok
		}
		assertTrue(
			varLib.assertLegalVariableID("Float", spScope, numberManager));
		try
		{
			varLib.getVariableID(eqInst, "Float");
			fail("undefined name (unrelated scope) must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//undefined, ok
		}

	}

	@Test
	public void testGetVID()
	{
		LegalScope globalScope = new SimpleLegalScope(null, "Global");
		varScopeLib.registerScope(globalScope);
		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		LegalScope eqScope = new SimpleLegalScope(globalScope, "Equipment");
		varScopeLib.registerScope(eqScope);
		SimpleVarScoped eq = new SimpleVarScoped();
		eq.scopeName = "Global.Equipment";
		eq.name = "Sword";
		ScopeInstance eqInst = instanceFactory.get("Global.Equipment", eq);
		LegalScope eqPartScope = new SimpleLegalScope(eqScope, "Part");
		varScopeLib.registerScope(eqPartScope);
		SimpleVarScoped eqpart = new SimpleVarScoped();
		eqpart.scopeName = "Global.Equipment.Part";
		eqpart.name = "Mod";
		eqpart.parent = eq;
		ScopeInstance eqPartInst = instanceFactory.get("Global.Equipment.Part", eqpart);
		assertTrue(
			varLib.assertLegalVariableID("Walk", globalScope, numberManager));
		assertTrue(
			varLib.assertLegalVariableID("Float", eqScope, numberManager));
		assertTrue(
			varLib.assertLegalVariableID("Hover", eqPartScope, numberManager));
		VariableID vid = varLib.getVariableID(globalInst, "Walk");
		assertEquals("Walk", vid.getName());
		assertEquals(globalInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = varLib.getVariableID(eqInst, "Float");
		assertEquals("Float", vid.getName());
		assertEquals(eqInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = varLib.getVariableID(eqInst, "Walk");
		assertEquals("Walk", vid.getName());
		//NOTE: Global scope here even though eqScope was passed into getVariableID
		assertEquals(globalInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = varLib.getVariableID(eqPartInst, "Walk");
		assertEquals("Walk", vid.getName());
		//NOTE: Global scope here even though eqPartScope was passed into getVariableID
		assertEquals(globalInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = varLib.getVariableID(eqPartInst, "Hover");
		assertEquals("Hover", vid.getName());
		assertEquals(eqPartInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = varLib.getVariableID(eqPartInst, "Hover");
		assertEquals("Hover", vid.getName());
		assertEquals(eqPartInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());
	}

	@Test
	public void testGetVariableFormat()
	{
		LegalScope globalScope = new SimpleLegalScope(null, "Global");
		LegalScope eqScope = new SimpleLegalScope(globalScope, "Equipment");
		assertTrue(
			varLib.assertLegalVariableID("Walk", globalScope, numberManager));
		assertTrue(
			varLib.assertLegalVariableID("Float", eqScope, numberManager));
		try
		{
			varLib.getVariableFormat(null, "Walk");
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			Object o = varLib.getVariableFormat(globalScope, null);
			assertTrue(o == null);
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok too
		}
		assertTrue(numberManager
			.equals(varLib.getVariableFormat(globalScope, "Walk")));
		assertTrue(
			numberManager.equals(varLib.getVariableFormat(eqScope, "Float")));
		//fail at depth
		assertTrue(
			numberManager.equals(varLib.getVariableFormat(eqScope, "Walk")));
		//work indirect
		assertTrue(varLib.getVariableFormat(globalScope, "Float") == null);
	}

	@Test
	public void testProveReuse()
	{
		BooleanManager booleanManager = FormatUtilities.BOOLEAN_MANAGER;
		SimpleLegalScope globalScope = new SimpleLegalScope(null, "Global");
		varScopeLib.registerScope(globalScope);
		LegalScope eqScope =
				new SimpleLegalScope(globalScope, "Equipment");
		varScopeLib.registerScope(eqScope);
		SimpleVarScoped eq = new SimpleVarScoped();
		eq.scopeName = "Global.Equipment";
		eq.name = "Sword";
		ScopeInstance eqInst = instanceFactory.get("Global.Equipment", eq);
		LegalScope abScope = new SimpleLegalScope(globalScope, "Ability");
		varScopeLib.registerScope(abScope);
		SimpleVarScoped ab = new SimpleVarScoped();
		ab.scopeName = "Global.Ability";
		ab.name = "Dodge";
		ScopeInstance abInst = instanceFactory.get("Global.Ability", ab);

		assertTrue(
			varLib.assertLegalVariableID("Walk", eqScope, numberManager));
		VariableID vidm = varLib.getVariableID(eqInst, "Walk");
		assertEquals("Walk", vidm.getName());
		assertEquals(eqInst, vidm.getScope());
		assertEquals(Number.class, vidm.getVariableFormat());

		assertTrue(
			varLib.assertLegalVariableID("Walk", abScope, booleanManager));
		VariableID vidf = varLib.getVariableID(abInst, "Walk");
		assertEquals("Walk", vidf.getName());
		assertEquals(abInst, vidf.getScope());
		assertEquals(Boolean.class, vidf.getVariableFormat());

		assertFalse(vidm.equals(vidf));
		assertFalse(vidf.equals(vidm));

	}
	
}

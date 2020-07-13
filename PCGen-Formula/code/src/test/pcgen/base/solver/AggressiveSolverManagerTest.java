/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Optional;

import org.junit.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.inst.ComplexNEPFormula;
import pcgen.base.formula.inst.SimpleLegalScope;
import pcgen.base.solver.testsupport.AbstractModifier;
import pcgen.base.solver.testsupport.AbstractSolverManagerTest;
import pcgen.base.solver.testsupport.MockStat;

public class AggressiveSolverManagerTest extends AbstractSolverManagerTest
{
	private ManagerFactory managerFactory = new ManagerFactory(){};
	private AggressiveSolverManager manager;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		manager = new AggressiveSolverManager(getFormulaManager(), managerFactory,
			getSolverFactory(), getVariableStore());
	}

	@SuppressWarnings("unused")
	@Test
	public void testIllegalConstruction()
	{
		try
		{
			new AggressiveSolverManager(null, managerFactory, getSolverFactory(), getVariableStore());
			fail("No nulls in constructor");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		FormulaManager formulaManager = getFormulaManager();
		try
		{
			new AggressiveSolverManager(formulaManager, null, getSolverFactory(), getVariableStore());
			fail("No nulls in constructor");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			new AggressiveSolverManager(formulaManager, managerFactory, getSolverFactory(), null);
			fail("No nulls in constructor");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			new AggressiveSolverManager(formulaManager, managerFactory, getSolverFactory(), null);
			fail("No nulls in constructor");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
	}

	@Test
	public void testTrivial()
	{
		getVariableLibrary().assertLegalVariableID("Limbs",
			getInstanceFactory().getScope("Global"), FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> limbs = (VariableID<Number>) getVariableLibrary()
			.getVariableID(getGlobalScopeInst(), "Limbs");
		manager.solveChildren(limbs);
	}

	@Override
	protected SolverManager getManager()
	{
		return manager;
	}

	@Test
	public void testAddModifierExternal()
	{
		WriteableVariableStore store = getVariableStore();
		LegalScope globalScope = getInstanceFactory().getScope("Global");
		ScopeInstance globalScopeInst = getGlobalScopeInst();
		getVariableLibrary().assertLegalVariableID("STR", globalScope,
			FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> str =
				(VariableID<Number>) getVariableLibrary().getVariableID(globalScopeInst,
					"STR");

		assertEquals(null, store.get(str));
		manager.createChannel(str);
		assertEquals(0, store.get(str));

		SimpleLegalScope localScope = new SimpleLegalScope(globalScope, "STAT");
		getScopeManager().registerScope(localScope);

		ScopeInstance strInst =
				getInstanceFactory().get("Global.STAT", Optional.of(new MockStat("Strength")));

		getVariableLibrary().assertLegalVariableID("Mod", localScope,
			FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> mod =
				(VariableID<Number>) getVariableLibrary().getVariableID(strInst, "Mod");
		
		AbstractModifier<Number> modifier = AbstractModifier.setNumber(3, 5);
		manager.addModifier(mod, modifier, strInst);
		assertEquals(3, store.get(mod));

		ComplexNEPFormula<Number> formula =
				new ComplexNEPFormula<>("mod", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> modMod = AbstractModifier.add(formula, 100);

		manager.addModifier(str, modMod, strInst);
		assertEquals(3, store.get(str));
		
		manager.removeModifier(mod, modifier, strInst);
		assertEquals(0, store.get(str));
	}
}

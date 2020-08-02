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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.ImplementedScope;
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
	private AggressiveSolverManager manager;

	@BeforeEach
	@Override
	protected void setUp()
	{
		super.setUp();
		manager = new AggressiveSolverManager(getFormulaManager(), getManagerFactory(),
			getSolverFactory(), getVariableStore());
	}
	
	@AfterEach
	@Override
	protected void tearDown()
	{
		super.tearDown();
		manager = null;
	}

	@Test
	public void testIllegalConstruction()
	{
		assertThrows(NullPointerException.class, () -> new AggressiveSolverManager(null, getManagerFactory(), getSolverFactory(), getVariableStore()));
		FormulaManager formulaManager = getFormulaManager();
		assertThrows(NullPointerException.class, () -> new AggressiveSolverManager(formulaManager, null, getSolverFactory(), getVariableStore()));
		assertThrows(NullPointerException.class, () -> new AggressiveSolverManager(formulaManager, getManagerFactory(), getSolverFactory(), null));
		assertThrows(NullPointerException.class, () -> new AggressiveSolverManager(formulaManager, getManagerFactory(), getSolverFactory(), null));
	}

	@Test
	public void testTrivial()
	{
		assertLegalVariable("Limbs", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> limbs = (VariableID<Number>) getVariableLibrary()
			.getVariableID(getGlobalScopeInst(), "Limbs");
		manager.solveChildren(limbs);
	}

	@Override
	protected SolverSystem getManager()
	{
		return manager;
	}

	@Test
	public void testAddModifierExternal()
	{
		WriteableVariableStore store = getVariableStore();
		ImplementedScope globalScope = getScopeManager().getImplementedScope("Global");
		ScopeInstance globalScopeInst = getGlobalScopeInst();
		assertLegalVariable("STR", "Global", FormatUtilities.NUMBER_MANAGER);
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

		assertLegalVariable("Mod", "Global.STAT", FormatUtilities.NUMBER_MANAGER);
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

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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.inst.ComplexNEPFormula;
import pcgen.base.solver.testsupport.AbstractModifier;
import pcgen.base.solver.testsupport.AbstractSolverManagerTest;
import pcgen.base.testsupport.SimpleVarScoped;

public class AggressiveSolverManagerTest extends AbstractSolverManagerTest
{
	private GeneralSolverSystem manager;

	@BeforeEach
	@Override
	protected void setUp()
	{
		super.setUp();
		manager = SolverUtilities.buildStaticSolverSystem(getVariableLibrary(),
			getManagerFactory(), getValueStore(), getVariableStore());
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
		SimpleSolverManager newSolver = new SimpleSolverManager(
			getVariableLibrary()::isLegalVariableID,
			getManagerFactory(), getValueStore(),
			getVariableStore());
		SolverDependencyManager dm = new StaticSolverDependencyManager(
			getManagerFactory());
		SolverStrategy strategy =
				new AggressiveStrategy(dm::processForChildren, newSolver::processSolver);
		assertThrows(NullPointerException.class, () -> new GeneralSolverSystem(null, dm, strategy));
		assertThrows(NullPointerException.class, () -> new GeneralSolverSystem(newSolver, null, strategy));
		assertThrows(NullPointerException.class, () -> new GeneralSolverSystem(newSolver, dm, null));
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
		ScopeInstance globalScopeInst = getGlobalScopeInst();
		assertLegalVariable("STR", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> str =
				(VariableID<Number>) getVariableLibrary().getVariableID(globalScopeInst,
					"STR");

		assertEquals(null, store.get(str));

		getScopeManager().registerScope("Global", "STAT");

		ScopeInstance strInst =
				getScopeInstance("Global.STAT", new SimpleVarScoped("Strength",
					getGlobalVarScoped(), "Global.STAT"));

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

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
package pcgen.base.solver;

import java.util.Arrays;
import java.util.List;

import pcgen.base.calculation.PCGenModifier;
import pcgen.base.format.ArrayFormatManager;
import pcgen.base.format.NumberManager;
import pcgen.base.format.StringManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.LegalScopeLibrary;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.inst.SimpleFormulaManager;
import pcgen.base.formula.inst.SimpleFunctionLibrary;
import pcgen.base.formula.inst.SimpleLegalScope;
import pcgen.base.formula.inst.SimpleOperatorLibrary;
import pcgen.base.formula.inst.SimpleScopeInstance;
import pcgen.base.solver.testsupport.TrackingVariableCache;
import pcgen.base.util.FormatManager;
import pcgen.rules.persistence.token.ModifierFactory;

import org.junit.Before;
import org.junit.Test;
import plugin.modifier.set.AddModifierFactory;
import plugin.modifier.set.SetModifierFactory;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class SetSolverManagerTest
{
	private static final Class<String[]> STRING_ARRAY =
			(Class<String[]>) String[].class;
	private final LegalScope globalScope = new SimpleLegalScope(null, "Global");
	private FunctionLibrary fl;
	private OperatorLibrary ol;
	private TrackingVariableCache vc;
	private LegalScopeLibrary vsLib;
	private VariableLibrary sl;
	private FormulaManager fm;
	private AggressiveSolverManager manager;
	private final FormatManager<Number> numberManager = new NumberManager();
	private final FormatManager<String> stringManager = new StringManager();
	private ArrayFormatManager<String> arrayManager;

	@Before
	public void setUp() throws Exception
	{
		fl = new SimpleFunctionLibrary();
		ol = new SimpleOperatorLibrary();
		vc = new TrackingVariableCache();
		vsLib = new LegalScopeLibrary();
		sl = new VariableLibrary(vsLib);
		arrayManager = new ArrayFormatManager<>(stringManager, ',');
		ManagerFactory managerFactory = new ManagerFactory(){};
		fm = new SimpleFormulaManager(ol, sl, vc, new SolverFactory());
		fm = fm.getWith(FormulaManager.FUNCTION, fl);
		SolverFactory solverFactory = new SolverFactory();
		manager = new AggressiveSolverManager(fm, managerFactory, solverFactory, vc);
		ModifierFactory m = new SetModifierFactory();
		Modifier mod = m.getModifier(0, "", managerFactory, null, globalScope, arrayManager);
		solverFactory.addSolverFormat(STRING_ARRAY, mod);
	}

	@Test
	public void testProcessDependentSet()
	{
		sl.assertLegalVariableID("Regions", globalScope, arrayManager);
		ScopeInstance scopeInst =
				new SimpleScopeInstance(null, globalScope, "Global");
		VariableID<String[]> regions =
				(VariableID<String[]>) sl.getVariableID(scopeInst, "Regions");
		manager.createChannel(regions);
		Object[] array = vc.get(regions);
		List<Object> list = Arrays.asList(array);
		assertEquals(0, array.length);
		assertTrue(vc.set.contains(regions));
		assertEquals(1, vc.set.size());
		vc.reset();

		ModifierFactory am1 = new AddModifierFactory<>();
		PCGenModifier mod = am1.getModifier(2000, "France,England", new ManagerFactory(){}, null, globalScope, arrayManager);
		manager.addModifier(regions, mod, scopeInst);
		array = vc.get(regions);
		assertThat(2, is(array.length));
		list = Arrays.asList(array);
		assertTrue(list.contains("England"));
		assertTrue(list.contains("France"));
		assertTrue(vc.set.contains(regions));
		assertEquals(1, vc.set.size());
		vc.reset();

		ModifierFactory am2 = new AddModifierFactory<>();
		mod = am2.getModifier(3000, "Greece,England", new ManagerFactory(){}, null, globalScope, arrayManager);
		manager.addModifier(regions, mod, scopeInst);
		array = vc.get(regions);
		assertThat(3, is(array.length));
		list = Arrays.asList(array);
		assertTrue(list.contains("England"));
		assertTrue(list.contains("France"));
		assertTrue(list.contains("Greece"));
		assertTrue(vc.set.contains(regions));
		assertEquals(1, vc.set.size());
		vc.reset();
	}

}

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.inst.FormulaUtilities;
import pcgen.base.formula.inst.GlobalVarScoped;
import pcgen.base.formula.inst.ScopeManagerInst;
import pcgen.base.formula.inst.SimpleLegalScope;
import pcgen.base.formula.inst.SimpleOperatorLibrary;
import pcgen.base.solver.testsupport.AbstractModifier;
import pcgen.base.solver.testsupport.MockStat;
import pcgen.base.testsupport.TestUtilities;

public class SolverTest
{
	private FormulaManager formulaManager;
	private EvaluationManager evalManager;
	private ScopeInstance inst;
	private ScopeInstance str;
	private ScopeInstance con;

	@BeforeEach
	void setUp()
	{
		FormulaSetupFactory setup = new FormulaSetupFactory();
		ScopeManagerInst scopeManager = new ScopeManagerInst();
		SimpleLegalScope globalScope = new SimpleLegalScope("Global");
		scopeManager.registerScope(globalScope);
		scopeManager.registerScope(new SimpleLegalScope(globalScope, "STAT"));
		setup.setScopeManagerSupplier(() -> scopeManager);
		formulaManager = setup.generate();
		ScopeInstanceFactory scopeInstanceFactory = formulaManager.getScopeInstanceFactory();
		inst = scopeInstanceFactory.get("Global", Optional.of(new GlobalVarScoped("Global")));
		str = scopeInstanceFactory.get("Global.STAT", Optional.of(new MockStat("STR")));
		con = scopeInstanceFactory.get("Global.STAT", Optional.of(new MockStat("CON")));
		OperatorLibrary opLibrary = FormulaUtilities.loadBuiltInOperators(new SimpleOperatorLibrary());
		ManagerFactory managerFactory = new ManagerFactory(opLibrary);
		evalManager = managerFactory.generateEvaluationManager(formulaManager);
	}

	@AfterEach
	void tearDown()
	{
		formulaManager = null;
		evalManager = null;
		inst = null;
		str = null;
		con = null;
	}

	@Test
	public void testIllegalConstruction()
	{
		assertThrows(NullPointerException.class, () -> new Solver<Number>(FormatUtilities.NUMBER_MANAGER, null));
		assertThrows(NullPointerException.class, () -> new Solver<Number>(null, 4));
	}

	@Test
	public void testIllegalAdd()
	{
		Solver<Number> solver = new Solver<Number>(FormatUtilities.NUMBER_MANAGER, 6);
		Modifier<Number> mod = AbstractModifier.add(1, 100);
		assertThrows(NullPointerException.class, () -> solver.addModifier(null, inst));
		assertThrows(NullPointerException.class, () -> solver.addModifier(mod, null));
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void testIllegalAddGenericsViolation()
	{
		Solver<Number> solver = new Solver<Number>(FormatUtilities.NUMBER_MANAGER, 6);
		Modifier<String> badm = AbstractModifier.setString("");
		//have to be bad about generics to even get this to be set up to fail
		assertThrows(IllegalArgumentException.class, () -> solver.addModifier((Modifier) badm, inst));
	}

	@Test
	public void testIllegalRemove()
	{
		Solver<Number> solver = new Solver<Number>(FormatUtilities.NUMBER_MANAGER, 6);
		Modifier<Number> mod = AbstractModifier.add(1, 100);
		assertThrows(NullPointerException.class, () -> solver.removeModifier(null, inst));
		assertThrows(NullPointerException.class, () -> solver.removeModifier(mod, null));
	}

	@Test
	public void testIllegalRemoveFromSource()
	{
		Solver<Number> solver = new Solver<Number>(FormatUtilities.NUMBER_MANAGER, 6);
		assertThrows(NullPointerException.class, () -> solver.removeFromSource(null));
	}

	@Test
	public void testHarmless()
	{
		Modifier<Number> addm = AbstractModifier.add(1, 100);
		Solver<Number> solver = new Solver<Number>(FormatUtilities.NUMBER_MANAGER, 6);
		solver.removeModifier(addm, inst);
		assertEquals(Integer.valueOf(6), solver.process(evalManager));
	}

	@Test
	public void testRemoveFromSource()
	{
		Modifier<Number> addm = AbstractModifier.add(1, 100);
		Modifier<Number> multm = AbstractModifier.multiply(2, 100);
		Modifier<Number> setm = AbstractModifier.setNumber(4, 100);
		Solver<Number> solver = new Solver<Number>(FormatUtilities.NUMBER_MANAGER, 6);
		//harmless
		solver.removeFromSource(inst);
		assertEquals(Integer.valueOf(6), solver.process(evalManager));
		//now do real stuff
		solver.addModifier(addm, inst);
		solver.addModifier(multm, str);
		solver.addModifier(setm, inst);
		assertEquals(Integer.valueOf(9), solver.process(evalManager));
		solver.removeFromSource(inst);
		assertEquals(Integer.valueOf(12), solver.process(evalManager));
		//Harmless
		solver.removeFromSource(con);
		assertEquals(Integer.valueOf(12), solver.process(evalManager));
	}

	@Test
	public void testProcessSamePriority()
	{
		Modifier<Number> addm = AbstractModifier.add(1, 100);
		Modifier<Number> multm = AbstractModifier.multiply(2, 100);
		Modifier<Number> setm = AbstractModifier.setNumber(4, 100);
		Solver<Number> solver = new Solver<Number>(FormatUtilities.NUMBER_MANAGER, 6);
		solver.addModifier(addm, inst);
		solver.addModifier(multm, inst);
		solver.addModifier(setm, inst);
		assertEquals(Integer.valueOf(9), solver.process(evalManager));
		solver.removeModifier(addm, inst);
		assertEquals(Integer.valueOf(8), solver.process(evalManager));
	}

	@Test
	public void testProcessUserPriority1()
	{
		//Will be ignored due to later set
		Modifier<Number> addm = AbstractModifier.add(1, 100);
		Modifier<Number> setm = AbstractModifier.setNumber(4, 200);
		Modifier<Number> multm = AbstractModifier.multiply(2, 300);
		Solver<Number> solver = new Solver<Number>(FormatUtilities.NUMBER_MANAGER, 6);
		solver.addModifier(addm, inst);
		solver.addModifier(multm, inst);
		solver.addModifier(setm, inst);
		assertEquals(Integer.valueOf(8), solver.process(evalManager));
	}

	@Test
	public void testProcessUserPriority2()
	{
		Modifier<Number> addm = AbstractModifier.add(1, 100);
		Modifier<Number> multm = AbstractModifier.multiply(2, 300);
		Solver<Number> solver = new Solver<Number>(FormatUtilities.NUMBER_MANAGER, 6);
		solver.addModifier(addm, inst);
		solver.addModifier(multm, inst);
		assertEquals(Integer.valueOf(14), solver.process(evalManager));
	}

	@Test
	public void testDiagnose()
	{
		Modifier<Number> addm = AbstractModifier.add(1, 100);
		Modifier<Number> setm = AbstractModifier.setNumber(4, 100);
		Modifier<Number> multm = AbstractModifier.multiply(2, 100);
		Solver<Number> solver = new Solver<Number>(FormatUtilities.NUMBER_MANAGER, 6);
		List<ProcessStep<Number>> list = solver.diagnose(evalManager);
		assertNotNull(list);
		assertEquals(1, list.size());
		ProcessStep<Number> step = list.get(0);
		assertEquals("Default Value for NUMBER", step.getSourceInfo());
		assertEquals(6, step.getResult());
		assertEquals("DEFAULT", step.getModifier().getIdentification());
		assertEquals("6", step.getModifier().getInstructions());
		solver.addModifier(addm, inst);
		//Shouldn't be related (verify list is not reference semantic)
		assertEquals(1, list.size());
		solver.addModifier(multm, str);
		solver.addModifier(setm, inst);
		list = solver.diagnose(evalManager);
		assertEquals(4, list.size());
		step = list.get(0);
		assertEquals("Default Value for NUMBER", step.getSourceInfo());
		assertEquals(6, step.getResult());
		assertEquals("DEFAULT", step.getModifier().getIdentification());
		assertEquals("6", step.getModifier().getInstructions());
		step = list.get(1);
		assertEquals(inst, step.getSource());
		assertEquals(4, step.getResult());
		assertEquals(setm, step.getModifier());
		step = list.get(2);
		assertEquals(str, step.getSource());
		assertEquals(8, step.getResult());
		assertEquals(multm, step.getModifier());
		step = list.get(3);
		assertEquals(inst, step.getSource());
		assertEquals(9, step.getResult());
		assertEquals(addm, step.getModifier());

	}

	@Test
	public void testArrayMod()
	{
		Solver<Number[]> solver = new Solver<Number[]>(TestUtilities.NUMBER_ARRAY_MANAGER, new Number[]{});
		assertTrue(Arrays.equals(new Number[]{}, solver.process(evalManager)));
		Modifier<Number[]> add1 = AbstractModifier.addToArray(1, 10);
		solver.addModifier(add1, inst);
		assertTrue(Arrays.equals(new Number[]{1}, solver.process(evalManager)));
		Modifier<Number[]> add2 = AbstractModifier.addToArray(2, 11);
		solver.addModifier(add2, inst);
		assertTrue(Arrays.equals(new Number[]{1, 2}, solver.process(evalManager)));
		Modifier<Number[]> add3 = AbstractModifier.addToArray(3, 12);
		solver.addModifier(add3, inst);
		assertTrue(Arrays.equals(new Number[]{1, 2, 3}, solver.process(evalManager)));
		Modifier<Number> addm = AbstractModifier.add(1, 100);
		Modifier<Number[]> addTo1 = new ArrayComponentModifier<>(TestUtilities.NUMBER_ARRAY_MANAGER, 0, addm);
		solver.addModifier(addTo1, inst);
		assertTrue(Arrays.equals(new Number[]{2, 2, 3}, solver.process(evalManager)));
		Modifier<Number> multm = AbstractModifier.multiply(2, 100);
		Modifier<Number[]> multTo2 = new ArrayComponentModifier<>(TestUtilities.NUMBER_ARRAY_MANAGER, 1, multm);
		solver.addModifier(multTo2, inst);
		assertTrue(Arrays.equals(new Number[]{2, 4, 3}, solver.process(evalManager)));
		Modifier<Number> setm = AbstractModifier.setNumber(7, 100);
		Modifier<Number[]> setTo3 = new ArrayComponentModifier<>(TestUtilities.NUMBER_ARRAY_MANAGER, 2, setm);
		solver.addModifier(setTo3, inst);
		assertTrue(Arrays.equals(new Number[]{2, 4, 7}, solver.process(evalManager)));
		solver.removeModifier(add1, inst);
		assertTrue(Arrays.equals(new Number[]{3, 6}, solver.process(evalManager)));
	}

	@Test
	public void testArrayIndependenceAdd()
	{
		Solver<Number[]> solver = new Solver<Number[]>(TestUtilities.NUMBER_ARRAY_MANAGER, new Number[]{});
		assertTrue(Arrays.equals(new Number[]{}, solver.process(evalManager)));
		Modifier<Number[]> add1 = AbstractModifier.addToArray(1, 10);
		solver.addModifier(add1, inst);
		assertTrue(Arrays.equals(new Number[]{1}, solver.process(evalManager)));
		Modifier<Number[]> add2 = AbstractModifier.addToArray(2, 11);
		solver.addModifier(add2, inst);
		assertTrue(Arrays.equals(new Number[]{1, 2}, solver.process(evalManager)));
		Modifier<Number[]> add3 = AbstractModifier.addToArray(3, 12);
		solver.addModifier(add3, inst);
		assertTrue(Arrays.equals(new Number[]{1, 2, 3}, solver.process(evalManager)));
		Solver<Number[]> other = solver.createReplacement();
		assertTrue(Arrays.equals(new Number[]{1, 2, 3}, other.process(evalManager)));
		Modifier<Number> addm = AbstractModifier.add(1, 100);
		Modifier<Number[]> addTo1 = new ArrayComponentModifier<>(TestUtilities.NUMBER_ARRAY_MANAGER, 0, addm);
		solver.addModifier(addTo1, inst);
		assertTrue(Arrays.equals(new Number[]{2, 2, 3}, solver.process(evalManager)));
		assertTrue(Arrays.equals(new Number[]{1, 2, 3}, other.process(evalManager)));
		Modifier<Number> multm = AbstractModifier.multiply(6, 100);
		Modifier<Number[]> multTo2 = new ArrayComponentModifier<>(TestUtilities.NUMBER_ARRAY_MANAGER, 1, multm);
		other.addModifier(multTo2, inst);
		assertTrue(Arrays.equals(new Number[]{2, 2, 3}, solver.process(evalManager)));
		assertTrue(Arrays.equals(new Number[]{1, 12, 3}, other.process(evalManager)));
	}

	@Test
	public void testArrayIndependenceSource()
	{
		Modifier<Number> addm = AbstractModifier.add(1, 100);
		Modifier<Number> multm = AbstractModifier.multiply(2, 100);
		Modifier<Number> setm = AbstractModifier.setNumber(4, 100);
		Solver<Number> solver = new Solver<Number>(FormatUtilities.NUMBER_MANAGER, 6);
		assertEquals(Integer.valueOf(6), solver.process(evalManager));
		//now do real stuff
		solver.addModifier(addm, inst);
		solver.addModifier(multm, str);
		solver.addModifier(setm, inst);
		assertEquals(Integer.valueOf(9), solver.process(evalManager));
		Solver<Number> other = solver.createReplacement();
		assertEquals(Integer.valueOf(9), other.process(evalManager));
		solver.removeFromSource(inst);
		assertEquals(Integer.valueOf(12), solver.process(evalManager));
		assertEquals(Integer.valueOf(9), other.process(evalManager));
		other.removeFromSource(str);
		assertEquals(Integer.valueOf(12), solver.process(evalManager));
		assertEquals(Integer.valueOf(5), other.process(evalManager));
	}
}

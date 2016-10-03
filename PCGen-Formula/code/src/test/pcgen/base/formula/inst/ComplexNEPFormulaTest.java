package pcgen.base.formula.inst;

import java.util.List;

import junit.framework.TestCase;
import pcgen.base.format.BooleanManager;
import pcgen.base.format.NumberManager;
import pcgen.base.format.StringManager;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.analysis.ArgumentDependencyManager;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.LegalScopeLibrary;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.solver.IndividualSetup;
import pcgen.base.solver.SplitFormulaSetup;

public class ComplexNEPFormulaTest extends TestCase
{

	private ManagerFactory managerFactory = new ManagerFactory(){};

	public void testConstructor()
	{
		try
		{
			new ComplexNEPFormula(null);
			fail("Expected null formula text to fail");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			new ComplexNEPFormula("3+*5");
			fail("Expected bad formula text to fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testToString()
	{
		assertEquals("3+5", new ComplexNEPFormula("3+5").toString());
		assertEquals("3*5", new ComplexNEPFormula("3*5").toString());
		assertEquals("(3+5)*7", new ComplexNEPFormula("(3+5)*7").toString());
		assertEquals("a-b", new ComplexNEPFormula("a-b").toString());
		assertEquals("if(a>=b,5,9)",
			new ComplexNEPFormula("if(a>=b,5,9)").toString());
		assertEquals("if(a==b,5,-9)",
			new ComplexNEPFormula("if(a==b,5,-9)").toString());
		assertEquals("if(a||b,\"A\",\"B\")", new ComplexNEPFormula(
			"if(a||b,\"A\",\"B\")").toString());
		assertEquals("value()", new ComplexNEPFormula("value()").toString());
		assertEquals("3^5", new ComplexNEPFormula("3^5").toString());
		assertEquals("process[THIS]",
			new ComplexNEPFormula("process[THIS]").toString());
	}

	public void testIsValid()
	{
		SplitFormulaSetup setup = new SplitFormulaSetup();
		setup.loadBuiltIns();
		LegalScopeLibrary scopeLib = setup.getLegalScopeLibrary();
		SimpleLegalScope globalScope = new SimpleLegalScope(null, "Global");
		scopeLib.registerScope(globalScope);
		IndividualSetup indSetup = new IndividualSetup(setup, "Global");

		FormulaManager fm = indSetup.getFormulaManager();
		NumberManager numberMgr = FormatUtilities.NUMBER_MANAGER;
		BooleanManager booleanMgr = FormatUtilities.BOOLEAN_MANAGER;
		StringManager stringMgr = new StringManager();

		FormulaSemantics fs =
				managerFactory.generateFormulaSemantics(fm, globalScope, null);
		try
		{
			new ComplexNEPFormula("3+5").isValid(numberMgr, null);
			fail("Expected null FormulaSemantics to fail");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			new ComplexNEPFormula("3+5").isValid(null, fs);
			fail("Expected null FormatManager to fail");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}

		new ComplexNEPFormula("3+5").isValid(numberMgr, fs);
		assertEquals(true, fs.isValid());
		new ComplexNEPFormula("3*5").isValid(numberMgr, fs);
		assertEquals(true, fs.isValid());
		new ComplexNEPFormula("(3+5)*7").isValid(numberMgr, fs);
		assertEquals(true, fs.isValid());

		setup.getVariableLibrary().assertLegalVariableID("a", globalScope,
			numberMgr);
		setup.getVariableLibrary().assertLegalVariableID("b", globalScope,
			numberMgr);
		new ComplexNEPFormula("a-b").isValid(numberMgr, fs);
		assertEquals(true, fs.isValid());
		new ComplexNEPFormula("if(a>=b,5,9)").isValid(numberMgr, fs);
		assertEquals(true, fs.isValid());
		new ComplexNEPFormula("if(a==b,5,-9)").isValid(numberMgr, fs);
		assertEquals(true, fs.isValid());

		setup.getVariableLibrary().assertLegalVariableID("c", globalScope,
			booleanMgr);
		setup.getVariableLibrary().assertLegalVariableID("d", globalScope,
			booleanMgr);
		new ComplexNEPFormula("if(c||d,\"A\",\"B\")").isValid(stringMgr, fs);
		assertEquals(true, fs.isValid());

		fs.set(FormulaSemantics.INPUT_FORMAT, numberMgr);
		new ComplexNEPFormula("value()").isValid(numberMgr, fs);
		assertEquals(true, fs.isValid());
		new ComplexNEPFormula("3^5").isValid(numberMgr, fs);
		assertEquals(true, fs.isValid());
	}

	public void testGetDependencies()
	{
		SplitFormulaSetup setup = new SplitFormulaSetup();
		setup.loadBuiltIns();
		LegalScopeLibrary scopeLib = setup.getLegalScopeLibrary();
		SimpleLegalScope globalScope = new SimpleLegalScope(null, "Global");
		scopeLib.registerScope(globalScope);
		IndividualSetup indSetup = new IndividualSetup(setup, "Global");

		ScopeInstance globalInst = indSetup.getGlobalScopeInst();
		DependencyManager depManager = setupDM(indSetup);

		NumberManager numberMgr = FormatUtilities.NUMBER_MANAGER;
		BooleanManager booleanMgr = FormatUtilities.BOOLEAN_MANAGER;

		new ComplexNEPFormula("3+5").getDependencies(depManager);
		assertTrue(depManager.getVariables().isEmpty());
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());

		depManager = setupDM(indSetup);
		new ComplexNEPFormula("3*5").getDependencies(depManager);
		assertTrue(depManager.getVariables().isEmpty());
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());

		depManager = setupDM(indSetup);
		new ComplexNEPFormula("(3+5)*7").getDependencies(depManager);
		assertTrue(depManager.getVariables().isEmpty());
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());

		setup.getVariableLibrary().assertLegalVariableID("a", globalScope,
			numberMgr);
		setup.getVariableLibrary().assertLegalVariableID("b", globalScope,
			numberMgr);

		depManager = setupDM(indSetup);
		new ComplexNEPFormula("a-b").getDependencies(depManager);
		List<VariableID<?>> variables = depManager.getVariables();
		assertEquals(2, variables.size());
		assertTrue(variables.contains(new VariableID<>(globalInst, numberMgr,
			"a")));
		assertTrue(variables.contains(new VariableID<>(globalInst, numberMgr,
			"b")));
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());

		depManager = setupDM(indSetup);
		new ComplexNEPFormula("if(a>=b,5,9)").getDependencies(depManager);
		variables = depManager.getVariables();
		assertEquals(2, variables.size());
		assertTrue(variables.contains(new VariableID<>(globalInst, numberMgr,
			"a")));
		assertTrue(variables.contains(new VariableID<>(globalInst, numberMgr,
			"b")));
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());

		depManager = setupDM(indSetup);
		new ComplexNEPFormula("if(a==b,5,-9)").getDependencies(depManager);
		variables = depManager.getVariables();
		assertEquals(2, variables.size());
		assertTrue(variables.contains(new VariableID<>(globalInst, numberMgr,
			"a")));
		assertTrue(variables.contains(new VariableID<>(globalInst, numberMgr,
			"b")));
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());

		setup.getVariableLibrary().assertLegalVariableID("c", globalScope,
			booleanMgr);
		setup.getVariableLibrary().assertLegalVariableID("d", globalScope,
			booleanMgr);

		depManager = setupDM(indSetup);
		new ComplexNEPFormula("if(c||d,\"A\",\"B\")")
			.getDependencies(depManager);
		variables = depManager.getVariables();
		assertEquals(2, variables.size());
		assertTrue(variables.contains(new VariableID<>(globalInst, booleanMgr,
			"c")));
		assertTrue(variables.contains(new VariableID<>(globalInst, booleanMgr,
			"d")));
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());

		depManager = setupDM(indSetup);
		new ComplexNEPFormula("value()").getDependencies(depManager);
		assertTrue(depManager.getVariables().isEmpty());
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());

		depManager = setupDM(indSetup);
		new ComplexNEPFormula("3^5").getDependencies(depManager);
		assertTrue(depManager.getVariables().isEmpty());
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());
	}

	private DependencyManager setupDM(IndividualSetup indSetup)
	{
		DependencyManager dm = managerFactory.generateDependencyManager(
			indSetup.getFormulaManager(), indSetup.getGlobalScopeInst(), null);
		dm.set(ArgumentDependencyManager.KEY, new ArgumentDependencyManager());
		return dm;
	}

	public void testResolve()
	{
		SplitFormulaSetup setup = new SplitFormulaSetup();
		setup.loadBuiltIns();
		LegalScopeLibrary scopeLib = setup.getLegalScopeLibrary();
		SimpleLegalScope globalScope = new SimpleLegalScope(null, "Global");
		scopeLib.registerScope(globalScope);
		IndividualSetup indSetup = new IndividualSetup(setup, "Global");

		ScopeInstance globalInst = indSetup.getGlobalScopeInst();
		EvaluationManager evalManager = managerFactory.generateEvaluationManager(
			indSetup.getFormulaManager(), indSetup.getGlobalScopeInst(), Number.class);
		try
		{
			new ComplexNEPFormula("3+5").resolve(null);
			fail("Expected null FormulaManager to fail");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}

		NumberManager numberMgr = FormatUtilities.NUMBER_MANAGER;
		BooleanManager booleanMgr = FormatUtilities.BOOLEAN_MANAGER;

		assertEquals(8, new ComplexNEPFormula("3+5").resolve(evalManager));
		assertEquals(15, new ComplexNEPFormula("3*5").resolve(evalManager));
		assertEquals(56, new ComplexNEPFormula("(3+5)*7").resolve(evalManager));

		setup.getVariableLibrary().assertLegalVariableID("a", globalScope,
			numberMgr);
		setup.getVariableLibrary().assertLegalVariableID("b", globalScope,
			numberMgr);

		indSetup.getVariableStore().put(
			new VariableID<>(globalInst, numberMgr, "a"), 4);
		indSetup.getVariableStore().put(
			new VariableID<>(globalInst, numberMgr, "b"), 1);
		assertEquals(3, new ComplexNEPFormula("a-b").resolve(evalManager));

		assertEquals(5,
			new ComplexNEPFormula("if(a>=b,5,9)").resolve(evalManager));

		assertEquals(-9,
			new ComplexNEPFormula("if(a==b,5,-9)").resolve(evalManager));

		setup.getVariableLibrary().assertLegalVariableID("c", globalScope,
			booleanMgr);
		setup.getVariableLibrary().assertLegalVariableID("d", globalScope,
			booleanMgr);
		indSetup.getVariableStore().put(
			new VariableID<>(globalInst, booleanMgr, "c"), false);
		indSetup.getVariableStore().put(
			new VariableID<>(globalInst, booleanMgr, "d"), true);

		assertEquals("A",
			new ComplexNEPFormula("if(c||d,\"A\",\"B\")").resolve(evalManager));

		evalManager.set(EvaluationManager.INPUT, 4);
		assertEquals(4, new ComplexNEPFormula("value()").resolve(evalManager));
		assertEquals(243.0, new ComplexNEPFormula("3^5").resolve(evalManager));
	}
}

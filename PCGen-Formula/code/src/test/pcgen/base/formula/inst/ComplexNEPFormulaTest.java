package pcgen.base.formula.inst;

import java.util.List;

import pcgen.base.format.BooleanManager;
import pcgen.base.format.NumberManager;
import pcgen.base.format.StringManager;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.analysis.ArgumentDependencyManager;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.VariableID;
import pcgen.base.testsupport.AbstractFormulaTestCase;

public class ComplexNEPFormulaTest extends AbstractFormulaTestCase
{

	NumberManager numberMgr = FormatUtilities.NUMBER_MANAGER;
	BooleanManager booleanMgr = FormatUtilities.BOOLEAN_MANAGER;
	StringManager stringMgr = FormatUtilities.STRING_MANAGER;

	private ManagerFactory managerFactory = new ManagerFactory()
	{
	};

	public void testConstructor()
	{
		try
		{
			new ComplexNEPFormula<>(null);
			fail("Expected null formula text to fail");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			new ComplexNEPFormula<>("3+*5");
			fail("Expected bad formula text to fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testToString()
	{
		assertEquals("3+5", new ComplexNEPFormula<>("3+5").toString());
		assertEquals("3*5", new ComplexNEPFormula<>("3*5").toString());
		assertEquals("(3+5)*7", new ComplexNEPFormula<>("(3+5)*7").toString());
		assertEquals("a-b", new ComplexNEPFormula<>("a-b").toString());
		assertEquals("if(a>=b,5,9)", new ComplexNEPFormula<>("if(a>=b,5,9)").toString());
		assertEquals("if(a==b,5,-9)",
			new ComplexNEPFormula<>("if(a==b,5,-9)").toString());
		assertEquals("if(a||b,\"A\",\"B\")",
			new ComplexNEPFormula<>("if(a||b,\"A\",\"B\")").toString());
		assertEquals("value()", new ComplexNEPFormula<>("value()").toString());
		assertEquals("3^5", new ComplexNEPFormula<>("3^5").toString());
		assertEquals("process[THIS]",
			new ComplexNEPFormula<>("process[THIS]").toString());
	}

	public void testIsValid()
	{
		FormulaSemantics fs = managerFactory.generateFormulaSemantics(getFormulaManager(),
			getGlobalScope(), null);
		try
		{
			new ComplexNEPFormula<Number>("3+5").isValid(numberMgr, null);
			fail("Expected null FormulaSemantics to fail");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			new ComplexNEPFormula<>("3+5").isValid(null, fs);
			fail("Expected null FormatManager to fail");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}

		new ComplexNEPFormula<Number>("3+5").isValid(numberMgr, fs);
		assertEquals(true, fs.isValid());
		new ComplexNEPFormula<Number>("3*5").isValid(numberMgr, fs);
		assertEquals(true, fs.isValid());
		new ComplexNEPFormula<Number>("(3+5)*7").isValid(numberMgr, fs);
		assertEquals(true, fs.isValid());

		getVariableLibrary().assertLegalVariableID("a", getGlobalScope(), numberMgr);
		getVariableLibrary().assertLegalVariableID("b", getGlobalScope(), numberMgr);
		new ComplexNEPFormula<Number>("a-b").isValid(numberMgr, fs);
		assertEquals(true, fs.isValid());
		new ComplexNEPFormula<Number>("if(a>=b,5,9)").isValid(numberMgr, fs);
		assertEquals(true, fs.isValid());
		new ComplexNEPFormula<Number>("if(a==b,5,-9)").isValid(numberMgr, fs);
		assertEquals(true, fs.isValid());

		getVariableLibrary().assertLegalVariableID("c", getGlobalScope(), booleanMgr);
		getVariableLibrary().assertLegalVariableID("d", getGlobalScope(), booleanMgr);
		new ComplexNEPFormula<String>("if(c||d,\"A\",\"B\")").isValid(stringMgr, fs);
		assertEquals(true, fs.isValid());

		new ComplexNEPFormula<Number>("value()").isValid(numberMgr,
			fs.getWith(FormulaSemantics.INPUT_FORMAT, numberMgr));
		assertEquals(true, fs.isValid());
		new ComplexNEPFormula<Number>("3^5").isValid(numberMgr, fs);
		assertEquals(true, fs.isValid());
	}

	public void testGetDependencies()
	{
		DependencyManager depManager = setupDM();

		new ComplexNEPFormula<>("3+5").getDependencies(depManager);
		assertTrue(depManager.get(DependencyManager.VARIABLES).getVariables().isEmpty());
		assertEquals(-1,
			depManager.get(ArgumentDependencyManager.KEY).getMaximumArgument());

		depManager = setupDM();
		new ComplexNEPFormula<>("3*5").getDependencies(depManager);
		assertTrue(depManager.get(DependencyManager.VARIABLES).getVariables().isEmpty());
		assertEquals(-1,
			depManager.get(ArgumentDependencyManager.KEY).getMaximumArgument());

		depManager = setupDM();
		new ComplexNEPFormula<>("(3+5)*7").getDependencies(depManager);
		assertTrue(depManager.get(DependencyManager.VARIABLES).getVariables().isEmpty());
		assertEquals(-1,
			depManager.get(ArgumentDependencyManager.KEY).getMaximumArgument());

		getVariableLibrary().assertLegalVariableID("a", getGlobalScope(), numberMgr);
		getVariableLibrary().assertLegalVariableID("b", getGlobalScope(), numberMgr);

		depManager = setupDM();
		new ComplexNEPFormula<>("a-b").getDependencies(depManager);
		List<VariableID<?>> variables = depManager.get(DependencyManager.VARIABLES).getVariables();
		assertEquals(2, variables.size());
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), numberMgr, "a")));
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), numberMgr, "b")));
		assertEquals(-1,
			depManager.get(ArgumentDependencyManager.KEY).getMaximumArgument());

		depManager = setupDM();
		new ComplexNEPFormula<>("if(a>=b,5,9)").getDependencies(depManager);
		variables = depManager.get(DependencyManager.VARIABLES).getVariables();
		assertEquals(2, variables.size());
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), numberMgr, "a")));
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), numberMgr, "b")));
		assertEquals(-1,
			depManager.get(ArgumentDependencyManager.KEY).getMaximumArgument());

		depManager = setupDM();
		new ComplexNEPFormula<>("if(a==b,5,-9)").getDependencies(depManager);
		variables = depManager.get(DependencyManager.VARIABLES).getVariables();
		assertEquals(2, variables.size());
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), numberMgr, "a")));
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), numberMgr, "b")));
		assertEquals(-1,
			depManager.get(ArgumentDependencyManager.KEY).getMaximumArgument());

		getVariableLibrary().assertLegalVariableID("c", getGlobalScope(), booleanMgr);
		getVariableLibrary().assertLegalVariableID("d", getGlobalScope(), booleanMgr);

		depManager = setupDM();
		new ComplexNEPFormula<>("if(c||d,\"A\",\"B\")").getDependencies(depManager);
		variables = depManager.get(DependencyManager.VARIABLES).getVariables();
		assertEquals(2, variables.size());
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), booleanMgr, "c")));
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), booleanMgr, "d")));
		assertEquals(-1,
			depManager.get(ArgumentDependencyManager.KEY).getMaximumArgument());

		depManager = setupDM();
		new ComplexNEPFormula<>("value()").getDependencies(depManager);
		assertTrue(depManager.get(DependencyManager.VARIABLES).getVariables().isEmpty());
		assertEquals(-1,
			depManager.get(ArgumentDependencyManager.KEY).getMaximumArgument());

		depManager = setupDM();
		new ComplexNEPFormula<>("3^5").getDependencies(depManager);
		assertTrue(depManager.get(DependencyManager.VARIABLES).getVariables().isEmpty());
		assertEquals(-1,
			depManager.get(ArgumentDependencyManager.KEY).getMaximumArgument());
	}

	private DependencyManager setupDM()
	{
		DependencyManager dm = managerFactory
			.generateDependencyManager(getFormulaManager(), getGlobalScopeInst(), null);
		dm = managerFactory.withVariables(dm);
		return dm.getWith(ArgumentDependencyManager.KEY, new ArgumentDependencyManager());
	}

	public void testResolve()
	{
		EvaluationManager evalManager = generateManager();
		try
		{
			new ComplexNEPFormula<>("3+5").resolve(null);
			fail("Expected null FormulaManager to fail");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}

		assertEquals(8, new ComplexNEPFormula<>("3+5").resolve(evalManager));
		assertEquals(15, new ComplexNEPFormula<>("3*5").resolve(evalManager));
		assertEquals(56, new ComplexNEPFormula<>("(3+5)*7").resolve(evalManager));

		getVariableLibrary().assertLegalVariableID("a", getGlobalScope(), numberMgr);
		getVariableLibrary().assertLegalVariableID("b", getGlobalScope(), numberMgr);

		getVariableStore().put(new VariableID<>(getGlobalScopeInst(), numberMgr, "a"), 4);
		getVariableStore().put(new VariableID<>(getGlobalScopeInst(), numberMgr, "b"), 1);
		assertEquals(3, new ComplexNEPFormula<>("a-b").resolve(evalManager));

		assertEquals(5, new ComplexNEPFormula<>("if(a>=b,5,9)").resolve(evalManager));

		assertEquals(-9, new ComplexNEPFormula<>("if(a==b,5,-9)").resolve(evalManager));

		getVariableLibrary().assertLegalVariableID("c", getGlobalScope(), booleanMgr);
		getVariableLibrary().assertLegalVariableID("d", getGlobalScope(), booleanMgr);
		getVariableStore().put(new VariableID<>(getGlobalScopeInst(), booleanMgr, "c"),
			false);
		getVariableStore().put(new VariableID<>(getGlobalScopeInst(), booleanMgr, "d"),
			true);

		assertEquals("A",
			new ComplexNEPFormula<>("if(c||d,\"A\",\"B\")").resolve(evalManager));

		EvaluationManager manager = this.generateManager(4);
		assertEquals(4, new ComplexNEPFormula<>("value()").resolve(manager));
		assertEquals(243.0, new ComplexNEPFormula<>("3^5").resolve(evalManager));
	}
}

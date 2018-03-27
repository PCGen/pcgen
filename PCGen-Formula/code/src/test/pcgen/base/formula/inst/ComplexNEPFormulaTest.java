package pcgen.base.formula.inst;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

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
import pcgen.base.formula.base.VariableList;
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
			new ComplexNEPFormula<>(null, numberMgr);
			fail("Expected null formula text to fail");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			new ComplexNEPFormula<>("3+6", null);
			fail("Expected null format manager to fail");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			new ComplexNEPFormula<>("3+*5", numberMgr);
			fail("Expected bad formula text to fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testToString()
	{
		assertEquals("3+5", new ComplexNEPFormula<>("3+5", numberMgr).toString());
		assertEquals("3*5", new ComplexNEPFormula<>("3*5", numberMgr).toString());
		assertEquals("(3+5)*7", new ComplexNEPFormula<>("(3+5)*7", numberMgr).toString());
		assertEquals("a-b", new ComplexNEPFormula<>("a-b", numberMgr).toString());
		assertEquals("if(a>=b,5,9)",
			new ComplexNEPFormula<>("if(a>=b,5,9)", numberMgr).toString());
		assertEquals("if(a==b,5,-9)",
			new ComplexNEPFormula<>("if(a==b,5,-9)", numberMgr).toString());
		assertEquals("if(a||b,\"A\",\"B\")",
			new ComplexNEPFormula<>("if(a||b,\"A\",\"B\")", numberMgr).toString());
		assertEquals("value()", new ComplexNEPFormula<>("value()", numberMgr).toString());
		assertEquals("3^5", new ComplexNEPFormula<>("3^5", numberMgr).toString());
		assertEquals("process[THIS]",
			new ComplexNEPFormula<>("process[THIS]", numberMgr).toString());
	}

	public void testIsValid()
	{
		FormulaSemantics fs = getSemantics();
		try
		{
			new ComplexNEPFormula<Number>("3+5", numberMgr).isValid(null);
			fail("Expected null FormulaSemantics to fail");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}

		new ComplexNEPFormula<Number>("3+5", numberMgr).isValid(fs);
		assertEquals(true, fs.isValid());
		new ComplexNEPFormula<Number>("3*5", numberMgr).isValid(fs);
		assertEquals(true, fs.isValid());
		new ComplexNEPFormula<Number>("(3+5)*7", numberMgr).isValid(fs);
		assertEquals(true, fs.isValid());

		getVariableLibrary().assertLegalVariableID("a", getInstanceFactory().getScope("Global"), numberMgr);
		getVariableLibrary().assertLegalVariableID("b", getInstanceFactory().getScope("Global"), numberMgr);
		new ComplexNEPFormula<Number>("a-b", numberMgr).isValid(fs);
		assertEquals(true, fs.isValid());
		new ComplexNEPFormula<Number>("if(a>=b,5,9)", numberMgr).isValid(fs);
		assertEquals(true, fs.isValid());
		new ComplexNEPFormula<Number>("if(a==b,5,-9)", numberMgr).isValid(fs);
		assertEquals(true, fs.isValid());

		getVariableLibrary().assertLegalVariableID("c", getInstanceFactory().getScope("Global"), booleanMgr);
		getVariableLibrary().assertLegalVariableID("d", getInstanceFactory().getScope("Global"), booleanMgr);
		new ComplexNEPFormula<String>("if(c||d,\"A\",\"B\")", stringMgr).isValid(fs);
		assertEquals(true, fs.isValid());

		new ComplexNEPFormula<Number>("value()", numberMgr)
			.isValid(fs.getWith(FormulaSemantics.INPUT_FORMAT, numberMgr));
		assertEquals(true, fs.isValid());
		new ComplexNEPFormula<Number>("3^5", numberMgr).isValid(fs);
		assertEquals(true, fs.isValid());
	}

	public void testGetDependenciesNone()
	{
		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("3+5", numberMgr).getDependencies(depManager);
		assertTrue(potentialVariables.get().getVariables().isEmpty());
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	public void testGetDependenciesNoneToo()
	{
		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("3*5", numberMgr).getDependencies(depManager);
		assertTrue(potentialVariables.get().getVariables().isEmpty());
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	public void testGetDependenciesNoneLonger()
	{
		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("(3+5)*7", numberMgr).getDependencies(depManager);
		assertTrue(potentialVariables.get().getVariables().isEmpty());
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	public void testGetDependenciesVars()
	{
		getVariableLibrary().assertLegalVariableID("a",
			getInstanceFactory().getScope("Global"), numberMgr);
		getVariableLibrary().assertLegalVariableID("b",
			getInstanceFactory().getScope("Global"), numberMgr);

		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("a-b", numberMgr).getDependencies(depManager);
		List<VariableID<?>> variables = potentialVariables.get().getVariables();
		assertEquals(2, variables.size());
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), numberMgr, "a")));
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), numberMgr, "b")));
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	public void testGetDependenciesVarsIfOne()
	{
		getVariableLibrary().assertLegalVariableID("a",
			getInstanceFactory().getScope("Global"), numberMgr);
		getVariableLibrary().assertLegalVariableID("b",
			getInstanceFactory().getScope("Global"), numberMgr);
		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("if(a>=b,5,9)", numberMgr).getDependencies(depManager);
		List<VariableID<?>> variables = potentialVariables.get().getVariables();
		assertEquals(2, variables.size());
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), numberMgr, "a")));
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), numberMgr, "b")));
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	public void testGetDependenciesVarsIfTwo()
	{
		getVariableLibrary().assertLegalVariableID("a",
			getInstanceFactory().getScope("Global"), numberMgr);
		getVariableLibrary().assertLegalVariableID("b",
			getInstanceFactory().getScope("Global"), numberMgr);
		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("if(a==b,5,-9)", numberMgr).getDependencies(depManager);
		List<VariableID<?>> variables = potentialVariables.get().getVariables();
		assertEquals(2, variables.size());
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), numberMgr, "a")));
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), numberMgr, "b")));
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	public void testGetDependenciesVarsIfThree()
	{
		getVariableLibrary().assertLegalVariableID("c",
			getInstanceFactory().getScope("Global"), booleanMgr);
		getVariableLibrary().assertLegalVariableID("d",
			getInstanceFactory().getScope("Global"), booleanMgr);

		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("if(c||d,\"A\",\"B\")", stringMgr)
			.getDependencies(depManager);
		List<VariableID<?>> variables = potentialVariables.get().getVariables();
		assertEquals(2, variables.size());
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), booleanMgr, "c")));
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), booleanMgr, "d")));
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	public void testGetDependenciesValue()
	{
		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("value()", numberMgr).getDependencies(depManager
			.getWith(DependencyManager.INPUT_FORMAT, FormatUtilities.NUMBER_MANAGER));
		assertTrue(potentialVariables.get().getVariables().isEmpty());
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	private DependencyManager setupDM()
	{
		DependencyManager dm = managerFactory
			.generateDependencyManager(getFormulaManager(), getGlobalScopeInst());
		dm = managerFactory.withVariables(dm);
		return dm.getWith(ArgumentDependencyManager.KEY,
			Optional.of(new ArgumentDependencyManager()));
	}

	public void testResolve()
	{
		EvaluationManager evalManager = generateManager();
		try
		{
			new ComplexNEPFormula<>("3+5", numberMgr).resolve(null);
			fail("Expected null FormulaManager to fail");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}

		assertEquals(8, new ComplexNEPFormula<>("3+5", numberMgr).resolve(evalManager));
		assertEquals(15, new ComplexNEPFormula<>("3*5", numberMgr).resolve(evalManager));
		assertEquals(56,
			new ComplexNEPFormula<>("(3+5)*7", numberMgr).resolve(evalManager));

		getVariableLibrary().assertLegalVariableID("a", getInstanceFactory().getScope("Global"), numberMgr);
		getVariableLibrary().assertLegalVariableID("b", getInstanceFactory().getScope("Global"), numberMgr);

		getVariableStore().put(new VariableID<>(getGlobalScopeInst(), numberMgr, "a"), 4);
		getVariableStore().put(new VariableID<>(getGlobalScopeInst(), numberMgr, "b"), 1);
		assertEquals(3, new ComplexNEPFormula<>("a-b", numberMgr).resolve(evalManager));

		assertEquals(5,
			new ComplexNEPFormula<>("if(a>=b,5,9)", numberMgr).resolve(evalManager));

		assertEquals(-9,
			new ComplexNEPFormula<>("if(a==b,5,-9)", numberMgr).resolve(evalManager));

		getVariableLibrary().assertLegalVariableID("c", getInstanceFactory().getScope("Global"), booleanMgr);
		getVariableLibrary().assertLegalVariableID("d", getInstanceFactory().getScope("Global"), booleanMgr);
		getVariableStore().put(new VariableID<>(getGlobalScopeInst(), booleanMgr, "c"),
			false);
		getVariableStore().put(new VariableID<>(getGlobalScopeInst(), booleanMgr, "d"),
			true);

		assertEquals("A",
			new ComplexNEPFormula<>("if(c||d,\"A\",\"B\")", numberMgr).resolve(evalManager));

		EvaluationManager manager = this.generateManager(4);
		assertEquals(4, new ComplexNEPFormula<>("value()", numberMgr).resolve(manager));
		assertEquals(243.0, new ComplexNEPFormula<>("3^5", numberMgr).resolve(evalManager));
	}

	@Test
	public void testAssertion()
	{
		ComplexNEPFormula five = new ComplexNEPFormula("5", numberMgr);
		ComplexNEPFormula fiveMismatch = new ComplexNEPFormula("5", stringMgr);
		ComplexNEPFormula longWayAround = new ComplexNEPFormula("\"4\"", numberMgr);
		ComplexNEPFormula fiveString = new ComplexNEPFormula("\"4\"", stringMgr);
		ComplexNEPFormula notANumber = new ComplexNEPFormula("\"4,4\"", numberMgr);

		FormulaSemantics fs = getSemantics();
		five.isValid(fs);
		assertEquals(true, fs.isValid());

		fiveMismatch.isValid(fs);
		assertEquals(false, fs.isValid());

		fiveString.isValid(fs);
		assertEquals(true, fs.isValid());

		longWayAround.isValid(fs);
		assertEquals(false, fs.isValid());

		longWayAround.isValid(fs.getWith(FormulaSemantics.ASSERTED, Optional.of(numberMgr)));
		//Note this implicitly tests that the report survives the .getWith
		assertEquals(true, fs.isValid());

		fiveString.isValid(fs.getWith(FormulaSemantics.ASSERTED, Optional.of(numberMgr)));
		assertEquals(false, fs.isValid());

		notANumber.isValid(fs.getWith(FormulaSemantics.ASSERTED, Optional.of(numberMgr)));
		assertEquals(false, fs.isValid());
	}

	private FormulaSemantics getSemantics()
	{
		return managerFactory.generateFormulaSemantics(getFormulaManager(),
			getInstanceFactory().getScope("Global"));
	}
}

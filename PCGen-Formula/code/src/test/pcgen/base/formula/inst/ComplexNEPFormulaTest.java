package pcgen.base.formula.inst;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.analysis.ArgumentDependencyManager;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableList;
import pcgen.base.formula.exception.SemanticsException;
import pcgen.base.testsupport.AbstractFormulaTestCase;

public class ComplexNEPFormulaTest extends AbstractFormulaTestCase
{

	private ManagerFactory managerFactory = new ManagerFactory()
	{
	};

	@SuppressWarnings("unused")
	public void testConstructor()
	{
		try
		{
			new ComplexNEPFormula<>(null, FormatUtilities.NUMBER_MANAGER);
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
			new ComplexNEPFormula<>("3+*5", FormatUtilities.NUMBER_MANAGER);
			fail("Expected bad formula text to fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testToString()
	{
		assertEquals("3+5", new ComplexNEPFormula<>("3+5", FormatUtilities.NUMBER_MANAGER).toString());
		assertEquals("3*5", new ComplexNEPFormula<>("3*5", FormatUtilities.NUMBER_MANAGER).toString());
		assertEquals("(3+5)*7", new ComplexNEPFormula<>("(3+5)*7", FormatUtilities.NUMBER_MANAGER).toString());
		assertEquals("a-b", new ComplexNEPFormula<>("a-b", FormatUtilities.NUMBER_MANAGER).toString());
		assertEquals("if(a>=b,5,9)",
			new ComplexNEPFormula<>("if(a>=b,5,9)", FormatUtilities.NUMBER_MANAGER).toString());
		assertEquals("if(a==b,5,-9)",
			new ComplexNEPFormula<>("if(a==b,5,-9)", FormatUtilities.NUMBER_MANAGER).toString());
		assertEquals("if(a||b,\"A\",\"B\")",
			new ComplexNEPFormula<>("if(a||b,\"A\",\"B\")", FormatUtilities.NUMBER_MANAGER).toString());
		assertEquals("value()", new ComplexNEPFormula<>("value()", FormatUtilities.NUMBER_MANAGER).toString());
		assertEquals("3^5", new ComplexNEPFormula<>("3^5", FormatUtilities.NUMBER_MANAGER).toString());
		assertEquals("process[THIS]",
			new ComplexNEPFormula<>("process[THIS]", FormatUtilities.NUMBER_MANAGER).toString());
	}

	public void testIsValid()
	{
		FormulaSemantics fs = getSemantics();
		try
		{
			new ComplexNEPFormula<Number>("3+5", FormatUtilities.NUMBER_MANAGER).isValid(null);
			fail("Expected null FormulaSemantics to fail");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		catch (SemanticsException e)
		{
			fail("Failed for unknown reason: " + e.getMessage());
		}

		try {
			new ComplexNEPFormula<Number>("3+5", FormatUtilities.NUMBER_MANAGER).isValid(fs);
			new ComplexNEPFormula<Number>("3*5", FormatUtilities.NUMBER_MANAGER).isValid(fs);
			new ComplexNEPFormula<Number>("(3+5)*7", FormatUtilities.NUMBER_MANAGER).isValid(fs);
			
			getVariableLibrary().assertLegalVariableID("a", getInstanceFactory().getScope("Global"), FormatUtilities.NUMBER_MANAGER);
			getVariableLibrary().assertLegalVariableID("b", getInstanceFactory().getScope("Global"), FormatUtilities.NUMBER_MANAGER);
			new ComplexNEPFormula<Number>("a-b", FormatUtilities.NUMBER_MANAGER).isValid(fs);
			new ComplexNEPFormula<Number>("if(a>=b,5,9)", FormatUtilities.NUMBER_MANAGER).isValid(fs);
			new ComplexNEPFormula<Number>("if(a==b,5,-9)", FormatUtilities.NUMBER_MANAGER).isValid(fs);
			
			getVariableLibrary().assertLegalVariableID("c", getInstanceFactory().getScope("Global"), FormatUtilities.BOOLEAN_MANAGER);
			getVariableLibrary().assertLegalVariableID("d", getInstanceFactory().getScope("Global"), FormatUtilities.BOOLEAN_MANAGER);
			new ComplexNEPFormula<String>("if(c||d,\"A\",\"B\")", FormatUtilities.STRING_MANAGER).isValid(fs);
			
			new ComplexNEPFormula<Number>("value()", FormatUtilities.NUMBER_MANAGER).isValid(
				fs.getWith(FormulaSemantics.INPUT_FORMAT, Optional.of(FormatUtilities.NUMBER_MANAGER)));
			new ComplexNEPFormula<Number>("3^5", FormatUtilities.NUMBER_MANAGER).isValid(fs);
		}
		catch (SemanticsException e)
		{
			fail(e.getMessage());
		}
	}

	public void testGetDependenciesNone()
	{
		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("3+5", FormatUtilities.NUMBER_MANAGER).getDependencies(depManager);
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
		new ComplexNEPFormula<>("3*5", FormatUtilities.NUMBER_MANAGER).getDependencies(depManager);
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
		new ComplexNEPFormula<>("(3+5)*7", FormatUtilities.NUMBER_MANAGER).getDependencies(depManager);
		assertTrue(potentialVariables.get().getVariables().isEmpty());
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	public void testGetDependenciesVars()
	{
		getVariableLibrary().assertLegalVariableID("a",
			getInstanceFactory().getScope("Global"), FormatUtilities.NUMBER_MANAGER);
		getVariableLibrary().assertLegalVariableID("b",
			getInstanceFactory().getScope("Global"), FormatUtilities.NUMBER_MANAGER);

		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("a-b", FormatUtilities.NUMBER_MANAGER).getDependencies(depManager);
		List<VariableID<?>> variables = potentialVariables.get().getVariables();
		assertEquals(2, variables.size());
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), FormatUtilities.NUMBER_MANAGER, "a")));
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), FormatUtilities.NUMBER_MANAGER, "b")));
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	public void testGetDependenciesVarsIfOne()
	{
		getVariableLibrary().assertLegalVariableID("a",
			getInstanceFactory().getScope("Global"), FormatUtilities.NUMBER_MANAGER);
		getVariableLibrary().assertLegalVariableID("b",
			getInstanceFactory().getScope("Global"), FormatUtilities.NUMBER_MANAGER);
		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("if(a>=b,5,9)", FormatUtilities.NUMBER_MANAGER).getDependencies(depManager);
		List<VariableID<?>> variables = potentialVariables.get().getVariables();
		assertEquals(2, variables.size());
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), FormatUtilities.NUMBER_MANAGER, "a")));
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), FormatUtilities.NUMBER_MANAGER, "b")));
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	public void testGetDependenciesVarsIfTwo()
	{
		getVariableLibrary().assertLegalVariableID("a",
			getInstanceFactory().getScope("Global"), FormatUtilities.NUMBER_MANAGER);
		getVariableLibrary().assertLegalVariableID("b",
			getInstanceFactory().getScope("Global"), FormatUtilities.NUMBER_MANAGER);
		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("if(a==b,5,-9)", FormatUtilities.NUMBER_MANAGER).getDependencies(depManager);
		List<VariableID<?>> variables = potentialVariables.get().getVariables();
		assertEquals(2, variables.size());
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), FormatUtilities.NUMBER_MANAGER, "a")));
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), FormatUtilities.NUMBER_MANAGER, "b")));
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	public void testGetDependenciesVarsIfThree()
	{
		getVariableLibrary().assertLegalVariableID("c",
			getInstanceFactory().getScope("Global"), FormatUtilities.BOOLEAN_MANAGER);
		getVariableLibrary().assertLegalVariableID("d",
			getInstanceFactory().getScope("Global"), FormatUtilities.BOOLEAN_MANAGER);

		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("if(c||d,\"A\",\"B\")", FormatUtilities.STRING_MANAGER)
			.getDependencies(depManager);
		List<VariableID<?>> variables = potentialVariables.get().getVariables();
		assertEquals(2, variables.size());
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), FormatUtilities.BOOLEAN_MANAGER, "c")));
		assertTrue(
			variables.contains(new VariableID<>(getGlobalScopeInst(), FormatUtilities.BOOLEAN_MANAGER, "d")));
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	public void testGetDependenciesValue()
	{
		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("value()", FormatUtilities.NUMBER_MANAGER).getDependencies(depManager
			.getWith(DependencyManager.INPUT_FORMAT, Optional.of(FormatUtilities.NUMBER_MANAGER)));
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
			new ComplexNEPFormula<>("3+5", FormatUtilities.NUMBER_MANAGER).resolve(null);
			fail("Expected null FormulaManager to fail");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}

		assertEquals(8, new ComplexNEPFormula<>("3+5", FormatUtilities.NUMBER_MANAGER).resolve(evalManager));
		assertEquals(15, new ComplexNEPFormula<>("3*5", FormatUtilities.NUMBER_MANAGER).resolve(evalManager));
		assertEquals(56,
			new ComplexNEPFormula<>("(3+5)*7", FormatUtilities.NUMBER_MANAGER).resolve(evalManager));

		getVariableLibrary().assertLegalVariableID("a", getInstanceFactory().getScope("Global"), FormatUtilities.NUMBER_MANAGER);
		getVariableLibrary().assertLegalVariableID("b", getInstanceFactory().getScope("Global"), FormatUtilities.NUMBER_MANAGER);

		getVariableStore().put(new VariableID<>(getGlobalScopeInst(), FormatUtilities.NUMBER_MANAGER, "a"), 4);
		getVariableStore().put(new VariableID<>(getGlobalScopeInst(), FormatUtilities.NUMBER_MANAGER, "b"), 1);
		assertEquals(3, new ComplexNEPFormula<>("a-b", FormatUtilities.NUMBER_MANAGER).resolve(evalManager));

		assertEquals(5,
			new ComplexNEPFormula<>("if(a>=b,5,9)", FormatUtilities.NUMBER_MANAGER).resolve(evalManager));

		assertEquals(-9,
			new ComplexNEPFormula<>("if(a==b,5,-9)", FormatUtilities.NUMBER_MANAGER).resolve(evalManager));

		getVariableLibrary().assertLegalVariableID("c", getInstanceFactory().getScope("Global"), FormatUtilities.BOOLEAN_MANAGER);
		getVariableLibrary().assertLegalVariableID("d", getInstanceFactory().getScope("Global"), FormatUtilities.BOOLEAN_MANAGER);
		getVariableStore().put(new VariableID<>(getGlobalScopeInst(), FormatUtilities.BOOLEAN_MANAGER, "c"),
			false);
		getVariableStore().put(new VariableID<>(getGlobalScopeInst(), FormatUtilities.BOOLEAN_MANAGER, "d"),
			true);

		assertEquals("A",
			new ComplexNEPFormula<>("if(c||d,\"A\",\"B\")", FormatUtilities.NUMBER_MANAGER).resolve(evalManager));

		EvaluationManager manager = this.generateManager(4);
		assertEquals(4, new ComplexNEPFormula<>("value()", FormatUtilities.NUMBER_MANAGER).resolve(manager));
		assertEquals(243.0, new ComplexNEPFormula<>("3^5", FormatUtilities.NUMBER_MANAGER).resolve(evalManager));
	}

	@Test
	public void testAssertionNumberDirect() throws SemanticsException
	{
		ComplexNEPFormula<Number> five = new ComplexNEPFormula<>("5", FormatUtilities.NUMBER_MANAGER);
		FormulaSemantics fs = getSemantics();
		five.isValid(fs);
	}

	@Test
	public void testAssertionNumberAsString() throws SemanticsException
	{
		ComplexNEPFormula<String> fourString = new ComplexNEPFormula<>("\"4\"", FormatUtilities.STRING_MANAGER);
		FormulaSemantics fs = getSemantics();
		fourString.isValid(fs);
		try
		{
			fourString.isValid(fs.getWith(FormulaSemantics.ASSERTED, Optional.of(FormatUtilities.NUMBER_MANAGER)));
			fail("Expected the Conversion to pass for the result to "
				+ "not be the String the ComplexNEPFormula expected!");
		}
		catch (SemanticsException e)
		{
			//Expected
		}
	}

	@Test
	public void testAssertionNotANumber()
	{
		ComplexNEPFormula<Number> notANumber =
				new ComplexNEPFormula<>("\"4,4\"", FormatUtilities.NUMBER_MANAGER);

		FormulaSemantics fs = getSemantics();
		try
		{
			notANumber
				.isValid(fs.getWith(FormulaSemantics.ASSERTED, Optional.of(FormatUtilities.NUMBER_MANAGER)));
			fail("Expected non-number to fail");
		}
		catch (SemanticsException e)
		{
			//Expected
		}
	}

	@Test
	public void testAssertionFailMismatch()
	{
		ComplexNEPFormula<String> fiveMismatch = new ComplexNEPFormula<>("5", FormatUtilities.STRING_MANAGER);
		FormulaSemantics fs = getSemantics();
		try
		{
			fiveMismatch.isValid(fs);
			fail("Expected non-quoted item to fail as a String");
		}
		catch (SemanticsException e)
		{
			//Expected
		}
	}

	@Test
	public void testAssertionNumberAsStringConverted() throws SemanticsException
	{
		ComplexNEPFormula<Number> longWayAround =
				new ComplexNEPFormula<>("\"4\"", FormatUtilities.NUMBER_MANAGER);
		FormulaSemantics fs = getSemantics();
		try
		{
			longWayAround.isValid(fs);
			fail("Expected quoted item to fail as a number "
				+ "because not convertable without an assertion");
		}
		catch (SemanticsException e)
		{
			//Expected
		}
		longWayAround
			.isValid(fs.getWith(FormulaSemantics.ASSERTED, Optional.of(FormatUtilities.NUMBER_MANAGER)));
	}

	private FormulaSemantics getSemantics()
	{
		return managerFactory.generateFormulaSemantics(getFormulaManager(),
			getInstanceFactory().getScope("Global"));
	}
}

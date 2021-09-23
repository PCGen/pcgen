/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.inst;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.analysis.ArgumentDependencyManager;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableList;
import pcgen.base.formula.exception.SemanticsException;
import pcgen.base.testsupport.AbstractFormulaTestCase;

public class ComplexNEPFormulaTest extends AbstractFormulaTestCase
{

	@Test
	public void testConstructor()
	{
		assertThrows(NullPointerException.class, () -> new ComplexNEPFormula<>(null, FormatUtilities.NUMBER_MANAGER));
		assertThrows(NullPointerException.class, () -> new ComplexNEPFormula<>("3+6", null));
		assertThrows(IllegalArgumentException.class, () -> new ComplexNEPFormula<>("3+*5", FormatUtilities.NUMBER_MANAGER));
	}

	@Test
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

	@Test
	public void testIsValid()
	{
		FormulaSemantics fs = getSemantics();
		assertThrows(NullPointerException.class, () -> new ComplexNEPFormula<Number>("3+5", FormatUtilities.NUMBER_MANAGER).isValid(null));

		try {
			new ComplexNEPFormula<Number>("3+5", FormatUtilities.NUMBER_MANAGER).isValid(fs);
			new ComplexNEPFormula<Number>("3*5", FormatUtilities.NUMBER_MANAGER).isValid(fs);
			new ComplexNEPFormula<Number>("(3+5)*7", FormatUtilities.NUMBER_MANAGER).isValid(fs);
			
			assertLegalVariable("a", "Global", FormatUtilities.NUMBER_MANAGER);
			assertLegalVariable("b", "Global", FormatUtilities.NUMBER_MANAGER);
			new ComplexNEPFormula<Number>("a-b", FormatUtilities.NUMBER_MANAGER).isValid(fs);
			new ComplexNEPFormula<Number>("if(a>=b,5,9)", FormatUtilities.NUMBER_MANAGER).isValid(fs);
			new ComplexNEPFormula<Number>("if(a==b,5,-9)", FormatUtilities.NUMBER_MANAGER).isValid(fs);
			
			assertLegalVariable("c", "Global", FormatUtilities.BOOLEAN_MANAGER);
			assertLegalVariable("d", "Global", FormatUtilities.BOOLEAN_MANAGER);
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

	@Test
	public void testGetDependenciesNone()
	{
		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("3+5", FormatUtilities.NUMBER_MANAGER).captureDependencies(depManager);
		assertTrue(potentialVariables.get().getVariables().isEmpty());
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	@Test
	public void testGetDependenciesNoneToo()
	{
		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("3*5", FormatUtilities.NUMBER_MANAGER).captureDependencies(depManager);
		assertTrue(potentialVariables.get().getVariables().isEmpty());
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	@Test
	public void testGetDependenciesNoneLonger()
	{
		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("(3+5)*7", FormatUtilities.NUMBER_MANAGER).captureDependencies(depManager);
		assertTrue(potentialVariables.get().getVariables().isEmpty());
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	@Test
	public void testGetDependenciesVars()
	{
		assertLegalVariable("a", "Global", FormatUtilities.NUMBER_MANAGER);
		assertLegalVariable("b", "Global", FormatUtilities.NUMBER_MANAGER);

		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("a-b", FormatUtilities.NUMBER_MANAGER).captureDependencies(depManager);
		List<VariableID<?>> variables = potentialVariables.get().getVariables();
		assertEquals(2, variables.size());
		//Validate equality by constructing our own VariableIDs
		assertTrue(variables.contains(getVariable("a")));
		assertTrue(variables.contains(getVariable("b")));
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	@Test
	public void testGetDependenciesVarsIfOne()
	{
		assertLegalVariable("a", "Global", FormatUtilities.NUMBER_MANAGER);
		assertLegalVariable("b", "Global", FormatUtilities.NUMBER_MANAGER);
		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("if(a>=b,5,9)", FormatUtilities.NUMBER_MANAGER).captureDependencies(depManager);
		List<VariableID<?>> variables = potentialVariables.get().getVariables();
		assertEquals(2, variables.size());
		assertTrue(variables.contains(getVariable("a")));
		assertTrue(variables.contains(getVariable("b")));
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	@Test
	public void testGetDependenciesVarsIfTwo()
	{
		assertLegalVariable("a", "Global", FormatUtilities.NUMBER_MANAGER);
		assertLegalVariable("b", "Global", FormatUtilities.NUMBER_MANAGER);
		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("if(a==b,5,-9)", FormatUtilities.NUMBER_MANAGER).captureDependencies(depManager);
		List<VariableID<?>> variables = potentialVariables.get().getVariables();
		assertEquals(2, variables.size());
		assertTrue(variables.contains(getVariable("a")));
		assertTrue(variables.contains(getVariable("b")));
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	@Test
	public void testGetDependenciesVarsIfThree()
	{
		assertLegalVariable("c", "Global", FormatUtilities.BOOLEAN_MANAGER);
		assertLegalVariable("d", "Global", FormatUtilities.BOOLEAN_MANAGER);

		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("if(c||d,\"A\",\"B\")", FormatUtilities.STRING_MANAGER)
			.captureDependencies(depManager);
		List<VariableID<?>> variables = potentialVariables.get().getVariables();
		assertEquals(2, variables.size());
		assertTrue(variables.contains(getBooleanVariable("c")));
		assertTrue(variables.contains(getBooleanVariable("d")));
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	@Test
	public void testGetDependenciesValue()
	{
		DependencyManager depManager = setupDM();
		Optional<VariableList> potentialVariables =
				depManager.get(DependencyManager.VARIABLES);
		Optional<ArgumentDependencyManager> potentialArgDM =
				depManager.get(ArgumentDependencyManager.KEY);
		new ComplexNEPFormula<>("value()", FormatUtilities.NUMBER_MANAGER).captureDependencies(depManager
			.getWith(DependencyManager.INPUT_FORMAT, Optional.of(FormatUtilities.NUMBER_MANAGER)));
		assertTrue(potentialVariables.get().getVariables().isEmpty());
		assertEquals(-1, potentialArgDM.get().getMaximumArgument());
	}

	private DependencyManager setupDM()
	{
		DependencyManager dm = getManagerFactory()
			.generateDependencyManager(getGlobalScopeInst());
		dm = getManagerFactory().withVariables(dm);
		return dm.getWith(ArgumentDependencyManager.KEY,
			Optional.of(new ArgumentDependencyManager()));
	}

	public void testResolve()
	{
		EvaluationManager evalManager = generateManager();
		assertThrows(IllegalArgumentException.class, () -> new ComplexNEPFormula<>("3+5", FormatUtilities.NUMBER_MANAGER).resolve(null));

		assertEquals(8, new ComplexNEPFormula<>("3+5", FormatUtilities.NUMBER_MANAGER).resolve(evalManager));
		assertEquals(15, new ComplexNEPFormula<>("3*5", FormatUtilities.NUMBER_MANAGER).resolve(evalManager));
		assertEquals(56,
			new ComplexNEPFormula<>("(3+5)*7", FormatUtilities.NUMBER_MANAGER).resolve(evalManager));

		assertLegalVariable("a", "Global", FormatUtilities.NUMBER_MANAGER);
		assertLegalVariable("b", "Global", FormatUtilities.NUMBER_MANAGER);

		setVariable(getVariable("a"), 4);
		setVariable(getVariable("b"), 1);
		assertEquals(3, new ComplexNEPFormula<>("a-b", FormatUtilities.NUMBER_MANAGER).resolve(evalManager));

		assertEquals(5,
			new ComplexNEPFormula<>("if(a>=b,5,9)", FormatUtilities.NUMBER_MANAGER).resolve(evalManager));

		assertEquals(-9,
			new ComplexNEPFormula<>("if(a==b,5,-9)", FormatUtilities.NUMBER_MANAGER).resolve(evalManager));

		assertLegalVariable("c", "Global", FormatUtilities.BOOLEAN_MANAGER);
		assertLegalVariable("d", "Global", FormatUtilities.BOOLEAN_MANAGER);
		setVariable(getBooleanVariable("c"), false);
		setVariable(getBooleanVariable("d"), true);

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
		assertThrows(SemanticsException.class,
			() -> fourString.isValid(fs.getWith(FormulaSemantics.ASSERTED,
				Optional.of(FormatUtilities.NUMBER_MANAGER))),
			"Expected the Conversion to pass for the result to "
				+ "not be the String the ComplexNEPFormula expected!");
	}

	@Test
	public void testAssertionNotANumber()
	{
		ComplexNEPFormula<Number> notANumber =
				new ComplexNEPFormula<>("\"4,4\"", FormatUtilities.NUMBER_MANAGER);

		FormulaSemantics fs = getSemantics();
		assertThrows(SemanticsException.class,
			() -> notANumber.isValid(fs.getWith(FormulaSemantics.ASSERTED,
				Optional.of(FormatUtilities.NUMBER_MANAGER))),
			"Expected non-number to fail");
	}

	@Test
	public void testAssertionFailMismatch()
	{
		ComplexNEPFormula<String> fiveMismatch = new ComplexNEPFormula<>("5", FormatUtilities.STRING_MANAGER);
		FormulaSemantics fs = getSemantics();
		assertThrows(SemanticsException.class, () -> fiveMismatch.isValid(fs),
			"Expected non-quoted item to fail as a String");
	}

	@Test
	public void testAssertionNumberAsStringConverted() throws SemanticsException
	{
		ComplexNEPFormula<Number> longWayAround =
				new ComplexNEPFormula<>("\"4\"", FormatUtilities.NUMBER_MANAGER);
		FormulaSemantics fs = getSemantics();
		assertThrows(SemanticsException.class, () -> longWayAround.isValid(fs),
			"Expected quoted item to fail as a number "
				+ "because not convertable without an assertion");
		longWayAround
			.isValid(fs.getWith(FormulaSemantics.ASSERTED, Optional.of(FormatUtilities.NUMBER_MANAGER)));
	}

	@Test
	public void testEquality() throws SemanticsException
	{
		ComplexNEPFormula<Number> one =
				new ComplexNEPFormula<>("4+Arm", FormatUtilities.NUMBER_MANAGER);
		ComplexNEPFormula<Number> two =
				new ComplexNEPFormula<>("4+Arm", FormatUtilities.NUMBER_MANAGER);
		assertEquals(one, two);
	}

	private FormulaSemantics getSemantics()
	{
		return getManagerFactory().generateFormulaSemantics(
			getImplementedScope("Global"));
	}
}

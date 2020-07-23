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
package pcgen.base.formula.function;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formatmanager.OptionalFormatFactory;
import pcgen.base.formatmanager.SimpleFormatManagerLibrary;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.testsupport.AbstractFormulaTestCase;
import pcgen.base.testsupport.TestUtilities;
import pcgen.base.util.FormatManager;


public class GetOptionalFunctionTest extends AbstractFormulaTestCase
{

	SimpleFormatManagerLibrary library;
	FormatManager<Optional<Number>> formatManager;

	@SuppressWarnings("unchecked")
	@Override
	@BeforeEach
	protected void setUp()
	{
		super.setUp();
		library = new SimpleFormatManagerLibrary();
		FormatUtilities.loadDefaultFormats(library);
		FormatUtilities.loadDefaultFactories(library);
		formatManager = (FormatManager<Optional<Number>>) new OptionalFormatFactory()
			.build(Optional.empty(), Optional.of("NUMBER"), library);
	}

	@Test
	public void testInvalidTooManyArg()
	{
		getVariableStore().put(getOptionalVariable("a"), Optional.of(3));
		getVariableStore().put(getOptionalVariable("b"), Optional.of(3));
		String formula = "getOptional(a, b)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, formatManager, Optional.empty());
	}

	@Test
	public void testNotValidString()
	{
		String formula = "getOptional(\"ab\")";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, formatManager, Optional.empty());
	}

	@Test
	public void testNotValidNoVar()
	{
		String formula = "getOptional(ab)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, formatManager, Optional.empty());
	}

	@Test
	public void testVariable()
	{
		getVariableStore().put(getOptionalVariable("a"), Optional.of(3));
		String formula = "getOptional(a)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, formatManager, Optional.empty());
		isStatic(formula, node, false);
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(1, vars.size());
		VariableID<?> var = vars.get(0);
		assertEquals("a", var.getName());
		evaluatesTo(formatManager, formula, node, Integer.valueOf(3));
	}

	@Test
	public void testNullVariable()
	{
		getVariableStore().put(getOptionalVariable("a"), Optional.empty());
		String formula = "getOptional(a)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, formatManager, Optional.empty());
		isStatic(formula, node, false);
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(1, vars.size());
		VariableID<?> var = vars.get(0);
		assertEquals("a", var.getName());
		evaluatesTo(formatManager, formula, node, Integer.valueOf(0));
	}

	protected VariableID<Optional<Number>> getOptionalVariable(String formula)
	{
		VariableLibrary variableLibrary = getVariableLibrary();
		variableLibrary.assertLegalVariableID(formula,
			getInstanceFactory().getScope("Global"), formatManager);
		@SuppressWarnings("unchecked")
		VariableID<Optional<Number>> variableID =
				(VariableID<Optional<Number>>) variableLibrary
					.getVariableID(getGlobalScopeInst(), formula);
		return variableID;
	}

}

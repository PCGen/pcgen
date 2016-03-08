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
package pcgen.base.formula.function;

import org.junit.Test;

import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.operator.number.NumberEquals;
import pcgen.base.formula.operator.number.NumberGreaterThan;
import pcgen.base.formula.operator.number.NumberLessThan;
import pcgen.base.formula.operator.number.NumberMinus;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.testsupport.AbstractFormulaTestCase;
import pcgen.base.testsupport.TestUtilities;

public class ThisFunctionTest extends AbstractFormulaTestCase
{

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		getFunctionLibrary().addFunction(new ThisFunction());
		OperatorLibrary operatorLibrary = getOperatorLibrary();
		operatorLibrary.addAction(new NumberEquals());
		operatorLibrary.addAction(new NumberLessThan());
		operatorLibrary.addAction(new NumberGreaterThan());
		operatorLibrary.addAction(new NumberMinus());
	}

	@Test
	public void testInvalidTooManyArg()
	{
		String formula = "this(2)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
	}

	@Test
	public void testVariable3()
	{
		String formula = "this()";
		SimpleNode node = TestUtilities.doParse(formula);
		isStatic(formula, node, true);
		Object source = new Object();
		Object result = getScopeInfo().evaluate(node, Number.class, source);
		assertEquals(source, result);
	}
}

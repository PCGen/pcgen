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
package pcgen.base.formula.parse;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

class FormulaParserTest
{

	private SimpleNode doTest(String formula) throws ParseException
	{
		return new FormulaParser(new StringReader(formula)).query();
	}

	@Test
	void testIntegerPositive()
	{
		try
		{
			doTest("1");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testIntegerZero()
	{
		try
		{
			doTest("0");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testIntegerNegative()
	{
		try
		{
			doTest("-5");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testDoubleOne()
	{
		try
		{
			doTest("1.0");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testDoublePositive()
	{
		try
		{
			doTest("1.1");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testDoubleNegative()
	{
		try
		{
			doTest("-4.5");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testDoubleNegativeNoLeading()
	{
		try
		{
			doTest("-.5");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testDoublePositiveNoLeading()
	{
		try
		{
			doTest(".2");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testDoublePositiveNoTrailing()
	{
		/*
		 * Note: This "cannot" fail, but is intended to demonstrate that the
		 * case has been considered
		 */
		try
		{
			doTest("1.");
			//Good if it works
		}
		catch (ParseException e)
		{
			//We accept this as affordable failure
		}
	}

	@Test
	void testDoubleNegativeNoTrailing()
	{
		/*
		 * Note: This "cannot" fail, but is intended to demonstrate that the
		 * case has been considered
		 */
		try
		{
			doTest("-8.");
			//Good if it works
		}
		catch (ParseException e)
		{
			//We accept this as affordable failure
		}
	}

	@Test
	void testAdd()
	{
		try
		{
			doTest("2+3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testAddNoLeading()
	{
		try
		{
			doTest("+3");
			fail("Expected Parse Error with no leading value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testAddNoTrailing()
	{
		try
		{
			doTest("2+");
			fail("Expected Parse Error with no trailing value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testAddNegative()
	{
		try
		{
			doTest("3+-4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testAddMultiple()
	{
		try
		{
			doTest("1+2+3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	public void testSubtract()
	{
		try
		{
			doTest("2-3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testSubtractNoTrailing()
	{
		try
		{
			doTest("2-");
			fail("Expected Parse Error with no trailing value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testSubtractNegative()
	{
		try
		{
			doTest("3--4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testSubtractFromNegative()
	{
		try
		{
			doTest("-3-4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testSubtractMultiple()
	{
		try
		{
			doTest("1-2-3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	public void testAnd()
	{
		try
		{
			doTest("2&&3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testAndNoLeading()
	{
		try
		{
			doTest("&&3");
			fail("Expected Parse Error with no leading value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testAndNoTrailing()
	{
		try
		{
			doTest("2&&");
			fail("Expected Parse Error with no trailing value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testAndNegative()
	{
		try
		{
			doTest("3&&-4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testAndFromNegative()
	{
		try
		{
			doTest("-3&&4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testAndMultiple()
	{
		try
		{
			doTest("1&&2&&3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	public void testOr()
	{
		try
		{
			doTest("2||3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testOrNoLeading()
	{
		try
		{
			doTest("||3");
			fail("Expected Parse Error with no leading value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testOrNoTrailing()
	{
		try
		{
			doTest("2||");
			fail("Expected Parse Error with no trailing value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testOrNegative()
	{
		try
		{
			doTest("3||-4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testOrFromNegative()
	{
		try
		{
			doTest("-3||4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testOrMultiple()
	{
		try
		{
			doTest("1||2||3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	public void testEqual()
	{
		try
		{
			doTest("2==3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testEqualNoLeading()
	{
		try
		{
			doTest("==3");
			fail("Expected Parse Error with no leading value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testEqualNoTrailing()
	{
		try
		{
			doTest("2==");
			fail("Expected Parse Error with no trailing value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testEqualNegative()
	{
		try
		{
			doTest("3==-4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testEqualFromNegative()
	{
		try
		{
			doTest("-3==4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testEqualMultiple()
	{
		try
		{
			doTest("1==2==3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	public void testNotEqual()
	{
		try
		{
			doTest("2!=3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testNotEqualNoLeading()
	{
		try
		{
			doTest("!=3");
			fail("Expected Parse Error with no leading value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testNotEqualNoTrailing()
	{
		try
		{
			doTest("2!=");
			fail("Expected Parse Error with no trailing value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testNotEqualNegative()
	{
		try
		{
			doTest("3!=-4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testNotEqualFromNegative()
	{
		try
		{
			doTest("-3!=4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testNotEqualMultiple()
	{
		try
		{
			doTest("1!=2!=3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testLessThan()
	{
		try
		{
			doTest("2<3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testLessThanNoLeading()
	{
		try
		{
			doTest("<3");
			fail("Expected Parse Error with no leading value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testLessThanNoTrailing()
	{
		try
		{
			doTest("2<");
			fail("Expected Parse Error with no trailing value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testLessThanNegative()
	{
		try
		{
			doTest("3<-4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testLessThanFromNegative()
	{
		try
		{
			doTest("-3<4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testLessThanMultiple()
	{
		try
		{
			doTest("1<2<3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testGreaterThan()
	{
		try
		{
			doTest("2>3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testGreaterThanNoLeading()
	{
		try
		{
			doTest(">3");
			fail("Expected Parse Error with no leading value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testGreaterThanNoTrailing()
	{
		try
		{
			doTest("2>");
			fail("Expected Parse Error with no trailing value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testGreaterThanNegative()
	{
		try
		{
			doTest("3>-4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testGreaterThanFromNegative()
	{
		try
		{
			doTest("-3>4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testGreaterThanMultiple()
	{
		try
		{
			doTest("1>2>3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testLessThanOrEqualTo()
	{
		try
		{
			doTest("2<=3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testLessThanOrEqualToNoLeading()
	{
		try
		{
			doTest("<=3");
			fail("Expected Parse Error with no leading value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testLessThanOrEqualToNoTrailing()
	{
		try
		{
			doTest("2<=");
			fail("Expected Parse Error with no trailing value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testLessThanOrEqualToNegative()
	{
		try
		{
			doTest("3<=-4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testLessThanOrEqualToFromNegative()
	{
		try
		{
			doTest("-3<=4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testLessThanOrEqualToMultiple()
	{
		try
		{
			doTest("1<=2<=3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testGreaterThanOrEqualTo()
	{
		try
		{
			doTest("2>=3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testGreaterThanOrEqualToNoLeading()
	{
		try
		{
			doTest(">=3");
			fail("Expected Parse Error with no leading value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testGreaterThanOrEqualToNoTrailing()
	{
		try
		{
			doTest("2>=");
			fail("Expected Parse Error with no trailing value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testGreaterThanOrEqualToNegative()
	{
		try
		{
			doTest("3>=-4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testGreaterThanOrEqualToFromNegative()
	{
		try
		{
			doTest("-3>=4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testGreaterThanOrEqualToMultiple()
	{
		try
		{
			doTest("1>=2>=3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testMultiply()
	{
		try
		{
			doTest("2*3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testMultiplyNoLeading()
	{
		try
		{
			doTest("*3");
			fail("Expected Parse Error with no leading value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testMultiplyNoTrailing()
	{
		try
		{
			doTest("2*");
			fail("Expected Parse Error with no trailing value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testMultiplyNegative()
	{
		try
		{
			doTest("3*-4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testMultiplyMultiple()
	{
		try
		{
			doTest("1*2*3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testDivide()
	{
		try
		{
			doTest("2/3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testDivideNoLeading()
	{
		try
		{
			doTest("/3");
			fail("Expected Parse Error with no leading value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testDivideNoTrailing()
	{
		try
		{
			doTest("2/");
			fail("Expected Parse Error with no trailing value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testDivideNegative()
	{
		try
		{
			doTest("3/-4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testDivideMultiple()
	{
		try
		{
			doTest("1/2/3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testRemainder()
	{
		try
		{
			doTest("2%3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testRemainderNoLeading()
	{
		try
		{
			doTest("%3");
			fail("Expected Parse Error with no leading value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testRemainderNoTrailing()
	{
		try
		{
			doTest("2%");
			fail("Expected Parse Error with no trailing value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testRemainderNegative()
	{
		try
		{
			doTest("3%-4");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testRemainderMultiple()
	{
		try
		{
			doTest("1%2%3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testExponent()
	{
		try
		{
			doTest("2^3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testExponentNoLeading()
	{
		try
		{
			doTest("^3");
			fail("Expected Parse Error with no leading value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testExponentNoTrailing()
	{
		try
		{
			doTest("2^");
			fail("Expected Parse Error with no trailing value");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	//Our syntax is not perfect, but it's not like there aren't other ways to do this...
	//	@Test
	//	public void testExponentNegative()
	//	{
	//		try
	//		{
	//			doTest("3^-4");
	//		}
	//		catch (ParseException e)
	//		{
	//			fail("Encountered Unexpected Exception: " + e.getMessage());
	//		}
	//	}

	@Test
	void testExponentMultiple()
	{
		try
		{
			doTest("1^2^3");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testEmptyParens()
	{
		try
		{
			doTest("()");
			fail("Expected rejection of empty parenthesis");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testParens()
	{
		try
		{
			doTest("(2+3)");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testExtraParens()
	{
		try
		{
			doTest("(((2+3)))");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testMismatchedParenEnd()
	{
		try
		{
			doTest("2)");
			fail("Expected Parse Error with mismatched Parenthesis");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testMismatchedParenStart()
	{
		try
		{
			doTest("(3.4");
			fail("Expected Parse Error with mismatched Parenthesis");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testMismatchedParenStartWrap()
	{
		try
		{
			doTest("((3.2)");
			fail("Expected Parse Error with mismatched Parenthesis");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testMismatchedParenEndWrap()
	{
		try
		{
			doTest("(1.2))");
			fail("Expected Parse Error with mismatched Parenthesis");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testDoubleSign()
	{
		try
		{
			doTest("1++2");
			fail("Expected Parse Error with two signs");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testDoubleSign2()
	{
		try
		{
			doTest("1*/2");
			fail("Expected Parse Error with two signs");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testDoubleSign3()
	{
		try
		{
			doTest("1+^2");
			fail("Expected Parse Error with two signs");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testDoubleSign4()
	{
		try
		{
			doTest("1-&&2");
			fail("Expected Parse Error with two signs");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testDoubleSign5()
	{
		try
		{
			doTest("1&&||2");
			fail("Expected Parse Error with two signs");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testDoubleSign6()
	{
		try
		{
			doTest("1<=+2");
			fail("Expected Parse Error with two signs");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testDoubleSign7()
	{
		try
		{
			doTest("1^%2");
			fail("Expected Parse Error with two signs");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testDoubleSign8()
	{
		try
		{
			doTest("1-*2");
			fail("Expected Parse Error with two signs");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testDoubleSign9()
	{
		try
		{
			doTest("1&&==2");
			fail("Expected Parse Error with two signs");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testDoubleSign10()
	{
		try
		{
			doTest("1+%2");
			fail("Expected Parse Error with two signs");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testVariable()
	{
		try
		{
			doTest("variable");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testNotValidOutsideParens()
	{
		try
		{
			doTest("1,2");
			fail("Expected invalid formula to fail parse");
		}
		catch (ParseException e)
		{
			//Yep
		}
	}

	@Test
	void testVariable2()
	{
		try
		{
			doTest("variable1");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testVariableAdd()
	{
		try
		{
			doTest("variable+variable2");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testDotVariable()
	{
		try
		{
			doTest("variable.variable2");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testEmptyParenFunction()
	{
		try
		{
			doTest("ParenFunction()");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testParenFunctionWithVariables()
	{
		try
		{
			doTest("ParenFunction(var1,var2)");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testParenFunctionLeadingComma()
	{
		try
		{
			doTest("ParenFunction(,3)");
			fail("Expected ParenFunction to fail");
		}
		catch (ParseException e)
		{
			//Yep!
		}
	}

	@Test
	void testParenFunctionTrailingComma()
	{
		try
		{
			doTest("ParenFunction(4,)");
			fail("Expected ParenFunction to fail");
		}
		catch (ParseException e)
		{
			//Yep!
		}
	}

	@Test
	void testParenFunctionDoubleComma()
	{
		try
		{
			doTest("ParenFunction(5,,6)");
			fail("Expected ParenFunction to fail");
		}
		catch (ParseException e)
		{
			//Yep!
		}
	}

	@Test
	void testEmptyBracketFunction()
	{
		try
		{
			doTest("BracketFunction[]");
			fail("Expected Empty BracketFunction to fail");
		}
		catch (ParseException e)
		{
			//Yep!
		}
	}

	@Test
	void testBracketFunctionWithDotVariable()
	{
		try
		{
			doTest("BracketFunction[var1.var2]");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testBracketFunctionWithVariables()
	{
		try
		{
			doTest("BracketFunction[var1,var2]");
			fail("Bracket Functions do not allow arguments");
		}
		catch (ParseException e)
		{
			//Yep!
		}
	}

	@Test
	void testBracketFunctionLeadingComma()
	{
		try
		{
			doTest("BracketFunction[,3]");
			fail("Expected BracketFunction to fail");
		}
		catch (ParseException e)
		{
			//Yep!
		}
	}

	@Test
	void testBracketFunctionTrailingComma()
	{
		try
		{
			doTest("BracketFunction[4,]");
			fail("Expected BracketFunction to fail");
		}
		catch (ParseException e)
		{
			//Yep!
		}
	}

	@Test
	void testTooMayChildren()
	{
		try
		{
			doTest("(5,5)");
			fail("Expected BracketFunction to fail");
		}
		catch (ParseException e)
		{
			//Yep!
		}
	}

	@Test
	void testBracketFunctionDoubleComma()
	{
		try
		{
			doTest("ParenFunction[5,,6]");
			fail("Expected ParenFunction to fail");
		}
		catch (ParseException e)
		{
			//Yep!
		}
	}

	@Test
	void testSpaceInVar()
	{
		try
		{
			doTest("My Var");
		}
		catch (ParseException e)
		{
			/*
			 * This may be a strange result to people - the explanation is that
			 * we need to support spaces for things like "CL=Special Fighter".
			 * Since our parser doesn't see "My Var" as anything different than
			 * the CL=x, we need to support spaces in both...
			 */
			fail("Expected Var with space to pass");
		}
	}

	@Test
	void testSpaceInParenFunction()
	{
		try
		{
			doTest("My Function(5,6,7)");
		}
		catch (ParseException e)
		{
			/*
			 * This may be a strange result to people - the explanation is that
			 * we need to support spaces for things like "CL=Special Fighter".
			 * Since our parser doesn't see "My Function" as anything different
			 * than the CL=x, we need to support spaces in both...
			 */
			fail("Expected Function with space to fail");
		}
	}

	@Test
	void testSpaceInBracketFunction()
	{
		try
		{
			doTest("My Function[5]");
		}
		catch (ParseException e)
		{
			/*
			 * This may be a strange result to people - the explanation is that
			 * we need to support spaces for things like "CL=Special Fighter".
			 * Since our parser doesn't see "My Function" as anything different
			 * than the CL=x we need to support spaces in both...
			 */
			fail("Expected Function with space to pass");
		}
	}

	@Test
	void testFormulaQuotedString()
	{
		try
		{
			doTest("MyFunction(\"WithQuotedString\")");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testQuotedStringWithSpaces()
	{
		try
		{
			doTest("MyFunction(\"With Quoted String and Spaces\")");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testQuotedString()
	{
		try
		{
			doTest("\"Quoted String with Spaces\"");
		}
		catch (ParseException e)
		{
			fail("Encountered Unexpected Exception: " + e.getMessage());
		}
	}
}

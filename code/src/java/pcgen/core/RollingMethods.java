/*
 * Copyright 2001 (C) Mario Bonassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package pcgen.core;

import java.util.Arrays;
import java.util.Stack;
import java.util.Vector;
import java.util.stream.IntStream;

import pcgen.base.util.RandomUtil;
import pcgen.util.Logging;

import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.List;
import org.nfunk.jep.function.PostfixMathCommand;

public final class RollingMethods
{

	private RollingMethods()
	{
	}

	/**
	 * Roll <var>times</var> number of dice with <var>sides</var>
	 * shape.
	 *
	 * @param times int how many dice to roll?
	 * @param sides int what shape dice?
	 *
	 * @return int dice total
	 */
	public static int roll(final int times, final int sides)
	{
		return roll(times, sides, times, 0);
	}

	/**
	 * One random number between 1 and <var>sides</var>, good, for
	 * example, for rolling percentage dice.
	 *
	 * @param sides int what shape die?
	 *
	 * @return int die roll
	 */
	public static int roll(final int sides)
	{
		return RandomUtil.getRandomInt(sides) + 1;
	}

	/**
	 * Roll <var>times</var> dice with <var>sides</var> shape,
	 * sort them, and return the sum of only those listed in
	 * <var>keep</var> (0-indexed).
	 *
	 * @param times int how many dice to roll?
	 * @param sides int what shape dice?
	 * @param keep int[] which dice to keep (0-indexed)?
	 *
	 * @return int dice total
	 */
	public static int roll(int times, final int sides, final int[] keep)
	{
		// return roll (times, sides, keep, 0, 0);
		int[] ints = IntStream.generate(() -> RandomUtil.getRandomInt(sides)).limit(times).sorted().toArray();
		// keep the +1 at the end
		return Arrays.stream(keep).reduce(keep.length, (a, aKeep) -> a + ints[aKeep]);
	}

	private static int getLeftIndex(final StringBuilder expression, int startIndex)
	{
		int startIndex1 = startIndex;
		int parenth = 0;
		char c;
		do
		{
			startIndex1--;
			if (startIndex1 >= 0)
			{
				c = expression.charAt(startIndex1);
			}
			else
			{
				break;
			}
			if (c == ')')
			{
				parenth++;
			}
			else if (c == '(')
			{
				parenth--;
			}
		}
		while ((parenth > 0) || (c == 'd') || (c == '*') || (c == '/') || (c == ' ') || Character.isDigit(c));
		return startIndex1 + 1;
	}

	private static int getRightIndex(final StringBuilder expression, int startIndex)
	{
		int startIndex1 = startIndex;
		int parenth = 0;
		char c;
		do
		{
			startIndex1++;
			if (startIndex1 < expression.length())
			{
				c = expression.charAt(startIndex1);
			}
			else
			{
				break;
			}
			if (c == '(')
			{
				parenth++;
			}
			else if (c == ')')
			{
				parenth--;
			}
		}
		while ((parenth > 0) || (c == '*') || (c == '/') || (c == ' ') || Character.isDigit(c));
		return startIndex1;
	}

	/**
	 * Takes many forms including "2d6-2" and returns the result
	 * Whitespace is ignored; case insensitive;  Most simple math
	 * operations (including exponentiation) are supported
	 * Functions builtin include max, min, roll
	 *  Add new functions to DiceExpressionFunctions
	 *
	 * @param method String formatted string representing dice roll
	 *
	 * @return int dice total
	 */
	public static int roll(final String method)
	{
		int r = 0;

		if (method.length() <= 0)
		{
			return r;
		}
		final StringBuilder expression = new StringBuilder(method.replaceAll("d%", "1d100"));
		int index = expression.lastIndexOf("d");
		while (index != -1)
		{
			expression.insert(getRightIndex(expression, index), ')');
			expression.setCharAt(index, ',');
			expression.insert(getLeftIndex(expression, index), "roll(");
			index = expression.lastIndexOf("d", index + 4);
		}
		String exp = expression.toString();
		exp = exp.replaceAll("\\[", "list(").replaceAll("\\]", ")");
		final JEP jep = new JEP();
		jep.addStandardFunctions();
		jep.addFunction("roll", new Roll());
		jep.addFunction("top", new Top());
		jep.addFunction("reroll", new Reroll());
		jep.addFunction("list", new List());
		jep.parseExpression(exp);
		if (!jep.hasError())
		{
			r = (int) jep.getValue();
		}
		else
		{
			Logging.errorPrint("Bad dice: " + expression + ":" + jep.getErrorInfo());
		}
		return r;
	}

	/**
	 * Roll {{@code times}} 1d{{@code sides}}, reroll any result <= {{@code reroll}}.
	 * Add together the highest {{@code numToKeep}} dice then add {{@code modifier}}
	 * and return the result.
	 *
	 * @param times
	 * @param sides
	 * @param numToKeep
	 * @param reroll
	 * @return the result of the die roll
	 */
	private static int roll(final int times, final int sides, final int numToKeep, final int reroll)
	{
		return IntStream.generate(() -> roll(sides - reroll) + reroll).limit(times).sorted().skip(times - numToKeep)
			.sum();
	}

	/**
	 * 
	 * <p>This class forms the basis for the dJEP extensions to the JEP library.
	 * It evaluates a {@code ROLL} token, which is an operator that comes
	 * in precedence between multiplicative and additive operators.</p>
	 * 
	 * <p>The class receives two parameters, the number of rolls and the number of
	 * faces per die, as in "3d6," or whatever.  It initializes a random number
	 * generator, which must be a subclass of {@code edu.cornell.lassp.houle.RngPack.RandomElement}.
	 * A default randomizer class is provided that wraps the java.util.Random class.
	 * The class used may be changed via setRandomClassName.</p>
	 * 
	 * <p>The class provides very minimal retrieval of individual rolls, via the
	 * {@code getRolls()} method.</p>
	 */
	private static final class Roll extends PostfixMathCommand
	{

		/**
		 * 
		 * <p>The default (and only) constructor.  Sets the number
		 * of parameters for the JEP package, and calls {@code setRandom()} to
		 * initialize the randomizer.</p>
		 */
		private Roll()
		{
			numberOfParameters = -1;
		}

		/**
		 * <p>The run command for the JEP framework.  This receives the arguments
		 * to the operator as a stack, pops off the two it needs, does type checking,
		 * rolls the dice on the randomizer, and pushes the result back onto the stack.
		 * Logging is performed if it is turned on.</p>
		 */
		@SuppressWarnings({"UseOfObsoleteCollectionType", "PMD.ReplaceVectorWithList"})
		@Override
		public void run(final Stack stack) throws ParseException
		{
			// check the stack
			checkStack(stack);
			if (curNumberOfParameters < 2)
			{
				throw new ParseException("Too few parameters");
			}
			if (curNumberOfParameters > 4)
			{
				throw new ParseException("Too many parameters");
			}

			int numToKeep = 0;
			int[] keep = null;
			int reroll = 0;
			while (curNumberOfParameters > 2)
			{
				final Object param = stack.pop();
				if ((param instanceof Top.TopRolls) && (numToKeep == 0))
				{
					numToKeep = ((Top.TopRolls) param).getRolls();
				}
				else if ((param instanceof Reroll.Rerolls) && (reroll == 0))
				{
					reroll = ((Reroll.Rerolls) param).getRolls();
				}
				else if ((param instanceof Vector) && (curNumberOfParameters == 3))
				{
					if (numToKeep != 0)
					{
						throw new ParseException("Redundant Arugments");
					}
					if (reroll != 0)
					{
						throw new ParseException(
							"Reroll not compatable with " + "older syntax, use top(NUMBER) instead");
					}
					final Vector vec = (Vector) param;
					keep = new int[vec.size()];
					for (int x = 0; x < vec.size(); x++)
					{
						keep[x] = ((int) Math.round((Double) vec.get(x))) - 1;
					}
				}
				else
				{
					throw new ParseException("Invalid parameter type");
				}
				curNumberOfParameters--;
			}

			// get the parameter from the stack
			Object faces = stack.pop();
			final Object numberOfRolls = stack.pop();
			if (faces instanceof Vector)
			{
				final java.util.List vec = (java.util.List) faces;
				faces = vec.get(RandomUtil.getRandomInt(vec.size()));
			}
			// check whether the argument is of the right type
			if ((faces instanceof Double) && (numberOfRolls instanceof Double))
			{
				// calculate the result
				//Integer.MAX_VALUE
				final double dRolls = (Double) numberOfRolls;
				final double dFaces = (Double) faces;
				if ((dRolls > Integer.MAX_VALUE) || (dFaces > Integer.MAX_VALUE))
				{
					throw new ParseException("Values greater than " + Integer.MAX_VALUE + " not allowed.");
				}
				int iRolls = (int) Math.round((Double) numberOfRolls);
				int iFaces = (int) Math.round((Double) faces);
				if (numToKeep == 0)
				{
					numToKeep = iRolls;
				}
				double result;
				if (keep == null)
				{
					result = roll(iRolls, iFaces, numToKeep, reroll);
				}
				else
				{
					result = roll(iRolls, iFaces, keep);
				}
				// push the result on the inStack
				stack.push(result);
			}
			else
			{
				throw new ParseException("Invalid parameter type");
			}
		}

	}

	private static final class Top extends PostfixMathCommand
	{

		private Top()
		{
			numberOfParameters = 1;
		}

		@Override
		public void run(final Stack stack) throws ParseException
		{
			final Object param = stack.pop();
			if (param instanceof Double)
			{
				final double dRolls = (Double) param;
				if (dRolls > Integer.MAX_VALUE)
				{
					throw new ParseException("Values greater than " + Integer.MAX_VALUE + " not allowed.");
				}
				final int iRolls = (int) Math.round(dRolls);
				if (iRolls > 0)
				{
					stack.push(new TopRolls(iRolls));
				}
				else
				{
					throw new ParseException("Values less than 1 are not allowed");
				}
			}
			else
			{
				throw new ParseException("Invalid parameter type");
			}
		}

		static final class TopRolls
		{

			private final Integer rolls;

			private TopRolls(final Integer rolls)
			{
				this.rolls = rolls;
			}

			public Integer getRolls()
			{
				return rolls;
			}

		}
	}

	private static final class Reroll extends PostfixMathCommand
	{

		private Reroll()
		{
			numberOfParameters = 1;
		}

		@Override
		public void run(final Stack stack) throws ParseException
		{
			final Object param = stack.pop();
			if (param instanceof Double)
			{
				final double dRolls = (Double) param;
				if (dRolls > Integer.MAX_VALUE)
				{
					throw new ParseException("Values greater than " + Integer.MAX_VALUE + " not allowed.");
				}
				final int iRolls = (int) Math.round(dRolls);
				if (iRolls > 0)
				{
					stack.push(new Rerolls(iRolls));
				}
				else
				{
					throw new ParseException("Values less than 1 are not allowed");
				}
			}
			else
			{
				throw new ParseException("Invalid parameter type");
			}
		}

		static final class Rerolls
		{

			private final Integer rolls;

			private Rerolls(final Integer rolls)
			{
				this.rolls = rolls;
			}

			public Integer getRolls()
			{
				return rolls;
			}

		}
	}
}

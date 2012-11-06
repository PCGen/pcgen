/*
 * RollingMethods.java
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
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id$
 */
package pcgen.core;

import pcgen.util.Logging;

import java.util.Arrays;
import java.util.Stack;
import java.util.Vector;
import org.nfunk.jep.*;
import org.nfunk.jep.function.List;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * <code>RollingMethods</code>.
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Revision$
 */
public final class RollingMethods
{

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
        return roll(times, sides, times, 0, 0);
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
        return Globals.getRandomInt(sides) + 1;
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
        final int[] rolls = new int[times];

        while (--times >= 0)
        {
            rolls[times] = Globals.getRandomInt(sides);
        }

        java.util.Arrays.sort(rolls);

        int total = keep.length; // keep the +1 at the end

        for (int i = 0; i < keep.length; ++i)
        {
            total += rolls[keep[i]]; // 0-indexed
        }

        return total;
    }

    /**
     * Roll <var>times</var> bizarre dice.
     *
     * @param times int how many dice to roll?
     * @param shape int[] array of values of sides of die
     *
     * @return what the die says
     */
    public static int roll(int times, final int[] shape)
    {
        int total = 0;

        while (--times >= 0)
        {
            total += shape[Globals.getRandomInt(shape.length)];
        }

        return total;
    }

    /**
     * Roll <var>times</var> bizarre dice, keeping
     * <var>keep</keep> of them in ascending order.
     *
     * @param times int how many dice to roll?
     * @param shape int[] array of values of sides of die
     * @param keep int[] which dice to keep
     *
     * @return what the die says
     */
    public static int roll(int times, final int[] shape, final int[] keep)
    {
        final int[] rolls = new int[times];

        while (--times >= 0)
        {
            rolls[times] = shape[Globals.getRandomInt(shape.length)];
        }

        Arrays.sort(rolls);

        int total = 0;

        for (int i = 0; i < keep.length; ++i)
        {
            total += rolls[keep[i]]; // 0-indexed
        }

        return total;
    }

    private static int getLeftIndex(StringBuilder expression, int startIndex)
    {
        int parenth = 0;
        char c;
        do
        {
            startIndex--;
            if (startIndex >= 0)
            {
                c = expression.charAt(startIndex);
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
        } while (parenth > 0 || c == 'd' || c == '*' || c == '/' || c == ' ' ||
                Character.isDigit(c));
        return startIndex + 1;
    }

    private static int getRightIndex(StringBuilder expression, int startIndex)
    {
        int parenth = 0;
        char c;
        do
        {
            startIndex++;
            if (startIndex < expression.length())
            {
                c = expression.charAt(startIndex);
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
        } while (parenth > 0 || c == '*' || c == '/' || c == ' ' ||
                Character.isDigit(c));
        return startIndex;
    }

    /**
     * Takes many forms including "2d6-2" and returns the result
     * Whitespace is ignored; case insensitive;  Most simple math
     * operations (including exponentiation) are supported
     * Functions builtin include max, min, roll
     *  Add new functions to DiceExpressionFunctions
     *
     * @see pcgen.util.DiceExpression
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
        StringBuilder expression = new StringBuilder(method.replaceAll("d%",
                                                                       "1d100"));
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
        JEP jep = new JEP();
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
            Logging.errorPrint("Bad dice: " + expression + ":" +
                               jep.getErrorInfo());
        }
        return r;
    }

    /**
     * Roll {<code>times</code>} 1d{<code>sides</code>}, reroll any result <= {<code>reroll</code>}.
     * Add together the highest {<code>numToKeep</code>} dice then add {<code>modifier</code>}
     * and return the result.
     *
     * @param times
     * @param sides
     * @param numToKeep
     * @param reroll
     * @param modifier
     * @return the result of the die roll
     */
    private static int roll(
            final int times,
            final int sides,
            final int numToKeep,
            final int reroll,
            final int modifier)
    {
        final int[] dieRoll = new int[times];
        int total = 0;
        final int keep = (numToKeep > times) ? times : numToKeep;

        for (int i = 0; i < times; ++i)
        {
            dieRoll[i] = roll(sides - reroll) + reroll;
        }

        Arrays.sort(dieRoll);

        if (Logging.isDebugMode())
        {
            final StringBuffer rollString = new StringBuffer(times << 2);
            rollString.append(dieRoll[0]);

            if (times > 1)
            {
                for (int i = 1; i < times; ++i)
                {
                    rollString.append(" + ").append(dieRoll[i]);
                }
            }
            Logging.debugPrint(rollString.toString());
        }

        // Now add together the highest "keep" dice

        for (int j = times - keep; j < times; j++)
        {
            total += dieRoll[j];
        }

        return total + modifier;
    }

    /**
     * @author RossLodge
     * 
     * <p>This class forms the basis for the dJEP extensions to the JEP library.
     * It evaluates a <code>ROLL</code> token, which is an operator that comes
     * in precedence between multiplicative and additive operators.</p>
     * 
     * <p>The class receives two parameters, the number of rolls and the number of
     * faces per die, as in "3d6," or whatever.  It initializes a random number
     * generator, which must be a subclass of <code>edu.cornell.lassp.houle.RngPack.RandomElement</code>.
     * A default randomizer class is provided that wraps the java.util.Random class.
     * The class used may be changed via setRandomClassName.</p>
     * 
     * <p>The class provides very minimal retrieval of individual rolls, via the
     * <code>getRolls()</code> method.</p>
     */
    private static class Roll extends PostfixMathCommand
    {

        /**
         * 
         * <p>The default (and only) constructor.  Sets the number
         * of parameters for the JEP package, and calls <code>setRandom()</code> to
         * initialize the randomizer.</p>
         */
        public Roll()
        {
            numberOfParameters = -1;
        }

        /**
         * <p>The run command for the JEP framework.  This receives the arguments
         * to the operator as a stack, pops off the two it needs, does type checking,
         * rolls the dice on the randomizer, and pushes the result back onto the stack.
         * Logging is performed if it is turned on.</p>
         * 
         * @see org.nfunk.jep.function.PostfixMathCommandI#run(java.util.Stack)
         */
        @Override
        public void run(Stack inStack) throws ParseException
        {
            // check the stack
            checkStack(inStack);
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
                Object param = inStack.pop();
                if (param instanceof Top.TopRolls && numToKeep == 0)
                {
                    numToKeep = ((Top.TopRolls) param).getRolls();
                }
                else if (param instanceof Reroll.Rerolls && reroll == 0)
                {
                    reroll = ((Reroll.Rerolls) param).getRolls();
                }
                else if (param instanceof Vector && curNumberOfParameters == 3)
                {
                    if (numToKeep != 0)
                    {
                        throw new ParseException("Redundant Arugments");
                    }
                    if (reroll != 0)
                    {
                        throw new ParseException("Reroll not compatable with older syntax, use top(NUMBER) instead");
                    }
                    Vector vec = (Vector) param;
                    keep = new int[vec.size()];
                    for (int x = 0; x < vec.size(); x++)
                    {
                        keep[x] = ((int) Math.round(((Double) vec.get(x)).doubleValue())) -
                                1;
                    }
                }
                else
                {
                    throw new ParseException("Invalid parameter type");
                }
                curNumberOfParameters--;
            }

            // get the parameter from the stack
            Object faces = inStack.pop();
            Object numberOfRolls = inStack.pop();
            if (faces instanceof Vector)
            {
                Vector vec = (Vector) faces;
                faces = vec.get(Globals.getRandomInt(vec.size()));
            }
            // check whether the argument is of the right type
            if (faces instanceof Double &&
                    numberOfRolls instanceof Double)
            {
                // calculate the result
                //Integer.MAX_VALUE
                double dRolls = ((Double) numberOfRolls).doubleValue();
                double dFaces = ((Double) faces).doubleValue();
                if (dRolls > Integer.MAX_VALUE || dFaces >
                        Integer.MAX_VALUE)
                {
                    throw new ParseException("Values greater than " +
                                             Integer.MAX_VALUE +
                                             " not allowed.");
                }
                int iRolls = (int) Math.round(((Double) numberOfRolls).doubleValue());
                int iFaces = (int) Math.round(((Double) faces).doubleValue());
                if (numToKeep == 0)
                {
                    numToKeep = iRolls;
                }
                double result = 0;
                if (keep == null)
                {
                    result = roll(iRolls, iFaces, numToKeep, reroll, 0);
                }
                else
                {
                    result = roll(iRolls, iFaces, keep);
                }
                // push the result on the inStack
                inStack.push(new Double(result));
            }
            else
            {
                throw new ParseException("Invalid parameter type");
            }
        }

    }

    private static class Top extends PostfixMathCommand
    {

        public Top()
        {
            numberOfParameters = 1;
        }

        @Override
        public void run(Stack inStack) throws ParseException
        {
            Object param = inStack.pop();
            if (param instanceof Double)
            {
                double dRolls = ((Double) param).doubleValue();
                if (dRolls > Integer.MAX_VALUE)
                {
                    throw new ParseException("Values greater than " +
                                             Integer.MAX_VALUE + " not allowed.");
                }
                int iRolls = (int) Math.round(dRolls);
                if (iRolls > 0)
                {
                    inStack.push(new TopRolls(iRolls));
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

        public static class TopRolls
        {

            private Integer rolls;

            public TopRolls(Integer rolls)
            {
                this.rolls = rolls;
            }

            public Integer getRolls()
            {
                return rolls;
            }

        }
    }

    private static class Reroll extends PostfixMathCommand
    {

        public Reroll()
        {
            numberOfParameters = 1;
        }

        @Override
        public void run(Stack inStack) throws ParseException
        {
            Object param = inStack.pop();
            if (param instanceof Double)
            {
                double dRolls = ((Double) param).doubleValue();
                if (dRolls > Integer.MAX_VALUE)
                {
                    throw new ParseException("Values greater than " +
                                             Integer.MAX_VALUE + " not allowed.");
                }
                int iRolls = (int) Math.round(dRolls);
                if (iRolls > 0)
                {
                    inStack.push(new Rerolls(iRolls));
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

        public static class Rerolls
        {

            private Integer rolls;

            public Rerolls(Integer rolls)
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

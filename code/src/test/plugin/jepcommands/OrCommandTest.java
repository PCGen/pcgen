/*
 * Copyright 2007 (C) andrew wilson <nuance@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.jepcommands;

import java.util.Stack;

import pcgen.PCGenTestCase;
import pcgen.util.testchecker.CompareEqualDouble;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommandI;

/**
 * <code>OrCommandTest</code> tests the functioning of the jep or plugin
 */
public class OrCommandTest extends PCGenTestCase
{

	/**
	 * Quick test suite creation - adds all methods beginning with "test"
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(OrCommandTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
    @Override
	protected void setUp() throws Exception
	{
		super.setUp();

    }

    private static boolean runOr(final Stack stack, final PostfixMathCommandI pCommand)
    {
        boolean b;
        try
        {
            pCommand.run(stack);
            b = true;
        }
        catch (ParseException e)
        {
            b = false;
        }
        return b;
    }

    /* Test the case where the first operand is true */
    public void testOr01()
    {
        final PostfixMathCommandI   c = new OrCommand();
        final Stack<Object>         s = new Stack<>();

        s.push(1.0);
        s.push(2.0);

        c.setCurNumberOfParameters(2);

        runOr(s, c);

        final Double result = (Double) s.pop();

        is(result, eq(1.0, 0.1), "if (1.0,2.0) returns 1.0");
    }

    /* Test the case where the first operand is false, but the second is true */
    public void testOr02()
    {
        final PostfixMathCommandI   c = new OrCommand();
        final Stack<Double>         s = new Stack<>();

        s.push(0.0);
        s.push(2.0);

        c.setCurNumberOfParameters(2);

        runOr(s, c);

        final Double result = s.pop();

        is(result, eq(2.0, 0.1), "if (0.0,2.0) returns 2.0");
    }

    /* Test the case where the first two operands are false*/
    public void testOr03()
    {
        final PostfixMathCommandI   c = new OrCommand();
        final Stack<Boolean>        s = new Stack<>();

        s.push(false);
        s.push(false);
        s.push(true);

        c.setCurNumberOfParameters(3);

        runOr(s, c);

        final Boolean result = s.pop();

        is(result, eq(true), "if (false,false,true) returns true");
    }

    /* Test the case where false and zero are skipped */
    public void testOr04()
    {
        final PostfixMathCommandI   c = new OrCommand();
        final Stack                 s = new Stack();

        s.push(0.0);
        s.push(false);
        s.push(true);

        c.setCurNumberOfParameters(3);

        runOr(s, c);

        final Object result = s.pop();

        is(result, eq(true), "if (0.0,false,true) returns true");
    }

    /* Test the case where false and zero are skipped */
    public void testOr05()
    {
        final PostfixMathCommandI   c = new OrCommand();
        final Stack                 s = new Stack();

        s.push(false);
        s.push(false);
        s.push(false);
        s.push(false);

        c.setCurNumberOfParameters(4);

        runOr(s, c);

        final Object result = s.pop();

        is(result, new CompareEqualDouble(0.0), "if (false,false,false,false) returns 0.0");
    }
}

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

import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import java.util.Stack;

import pcgen.PCGenTestCase;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommandI;

/**
 * {@code IfCommandTest} tests the functioning of the jep if plugin
 */
public class IfCommandTest extends PCGenTestCase
{

	/**
	 * Quick test suite creation - adds all methods beginning with "test"
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(IfCommandTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
    @Override
	protected void setUp() throws Exception
	{
		super.setUp();

    }

    private static boolean runIf(final Stack stack, final PostfixMathCommandI pCommand)
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

    /* Test the case where the condition is a zero double */
    public void testIf01()
    {
        final PostfixMathCommandI   c = new IfCommand();
        final Stack<Double>         s = new Stack<>();

        s.push(0.0);
        s.push(1.0);
        s.push(2.0);

        runIf(s, c);

        final Double result = s.pop();

        assertThat("if (0.0,1.0,2.0) returns 2.0", result, closeTo(2.0, 0.1));
    }

    /* Test the case where the condition is a non zero double */
    public void testIf02()
    {
        final PostfixMathCommandI   c = new IfCommand();
        final Stack<Double>         s = new Stack<>();

        s.push(1.0);
        s.push(1.0);
        s.push(2.0);

        runIf(s, c);

        final Double result = s.pop();

        assertThat("if (1.0,1.0,2.0) returns 1.0", result, closeTo(1.0, 0.1));
    }

    /* Test the case where the condition is a false boolean */
    public void testIf03()
    {
        final PostfixMathCommandI   c = new IfCommand();
        final Stack<Boolean>         s = new Stack<>();

        s.push(false);
        s.push(false);
        s.push(true);

        runIf(s, c);

        final Boolean result = s.pop();

        is(result, eq(true), "if (false,false,true) returns true");
    }

    /* Test the case where the condition is a true boolean */
    public void testIf04()
    {
        final PostfixMathCommandI   c = new IfCommand();
        final Stack<Boolean>         s = new Stack<>();

        s.push(true);
        s.push(false);
        s.push(true);

        runIf(s, c);

        final Boolean result = s.pop();

        is(result, eq(false), "if (true,false,true) returns false");
    }
}

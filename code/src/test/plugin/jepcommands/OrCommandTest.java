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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.util.Stack;

import org.junit.jupiter.api.Test;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommandI;

/**
 * {@code OrCommandTest} tests the functioning of the jep or plugin
 */
public class OrCommandTest
{
    private static void runOr(final Stack stack, final PostfixMathCommandI pCommand)
    {
        try
        {
            pCommand.run(stack);
        } catch (ParseException ignored)
        {
        }
    }

    /* Test the case where the first operand is true */
    @Test
    public void testOr01()
    {
        final PostfixMathCommandI c = new OrCommand();
        final Stack<Object> s = new Stack<>();

        s.push(1.0);
        s.push(2.0);

        c.setCurNumberOfParameters(2);

        runOr(s, c);

        final Double result = (Double) s.pop();

        assertThat("if (1.0,2.0) returns 1.0", result, closeTo(1.0, 0.1));
    }

    /* Test the case where the first operand is false, but the second is true */
    @Test
    public void testOr02()
    {
        final PostfixMathCommandI c = new OrCommand();
        final Stack<Double> s = new Stack<>();

        s.push(0.0);
        s.push(2.0);

        c.setCurNumberOfParameters(2);

        runOr(s, c);

        final Double result = s.pop();

        assertThat("if (0.0,2.0) returns 2.0", result, closeTo(2.0, 0.1));
    }

    /* Test the case where the first two operands are false*/
    @Test
    public void testOr03()
    {
        final PostfixMathCommandI c = new OrCommand();
        final Stack<Boolean> s = new Stack<>();

        s.push(false);
        s.push(false);
        s.push(true);

        c.setCurNumberOfParameters(3);

        runOr(s, c);

        final Boolean result = s.pop();

        assertThat("if (false,false,true) returns true", result, is(true));
    }

    /* Test the case where false and zero are skipped */
    @Test
    public void testOr04()
    {
        final PostfixMathCommandI c = new OrCommand();
        final Stack<Object> s = new Stack<>();

        s.push(0.0);
        s.push(false);
        s.push(true);

        c.setCurNumberOfParameters(3);

        runOr(s, c);

        final Object result = s.pop();

        assertThat("if (0.0,false,true) returns true", result, is(true));
    }

    /* Test the case where false and zero are skipped */
    @Test
    public void testOr05()
    {
        final PostfixMathCommandI c = new OrCommand();
        final Stack<Object> s = new Stack<>();

        s.push(false);
        s.push(false);
        s.push(false);
        s.push(false);

        c.setCurNumberOfParameters(4);

        runOr(s, c);

        final Object result = s.pop();

        assertThat("if (false,false,false,false) returns 0.0", (Double) result, is(closeTo(0.0, 0.1)));
    }
}

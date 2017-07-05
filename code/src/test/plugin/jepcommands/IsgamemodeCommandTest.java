/*
 * Copyright 2008 (C) James Dempsey
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.jepcommands;

import java.util.Stack;

import pcgen.PCGenTestCase;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommandI;

/**
 * The Class <code>IsgamemodeCommandTest</code> is responsible for checking 
 * that IsgamemodeCommand is working correctly. 
 * 
 * 
 */
public class IsgamemodeCommandTest extends PCGenTestCase
{

	/**
	 * Quick test suite creation - adds all methods beginning with "test".
	 * 
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(IsgamemodeCommandTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
    @Override
	protected void setUp() throws Exception
	{
		super.setUp();

    }

    /**
     * Run isgamemode.
     * 
     * @param stack the stack
     * 
     * @return true, if successful
     */
    private static boolean runIsgamemode(final Stack stack)
    {
        final PostfixMathCommandI   pCommand = new IsgamemodeCommand();
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

    /**
     * Test is game mode true.
     */
    public void testIsGameModeTrue()
    {
        final Stack<Object>         s = new Stack<>();

        s.push("3.5");

        runIsgamemode(s);

        final Integer result = (Integer) s.pop();

        is(result, eq(1), "isgamemode(\"3.5\") returns 1");
    }

    /**
     * Test is game mode false.
     */
    public void testIsGameModeFalse()
    {
        final Stack<Object>         s = new Stack<>();

        s.push("3e");

        runIsgamemode(s);

        final Integer result = (Integer) s.pop();

        is(result, eq(0), "isgamemode(\"3e\") returns 0");
    }
}

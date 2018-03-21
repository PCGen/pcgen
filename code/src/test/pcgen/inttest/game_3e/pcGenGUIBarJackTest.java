/*
 * Copyright 2015 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
 *
 * Created/Reinstated on 2015-11-24
 */
package pcgen.inttest.game_3e;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.inttest.PcgenFtlTestCase;

/**
 * Tests a 3e 4th lvl Gnome Barbarian
 */
@SuppressWarnings("nls")
public class pcGenGUIBarJackTest extends PcgenFtlTestCase
{

	/**
	 * Runs the test
	 * 
	 * @param args Ignored
	 */
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(pcGenGUIBarJackTest.class);
	}

	/**
	 * Default constructor
	 */
	public pcGenGUIBarJackTest()
	{
		super("3e_barjack");
	}

	/**
	 * standard JUnit style constructor
	 * @param name No idea
	 */
	public pcGenGUIBarJackTest(String name)
	{
		super(name);
	}

	/**
	 * Returns the test suite containing all the tests in this class.
	 * 
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIBarJackTest.class);
	}

	/**
	 * Main test case.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testBarJack() throws Exception
	{
		runTest("3e_BarJack", "3e");
	}
}

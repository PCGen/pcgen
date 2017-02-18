/*
 * pcGenGUICleElfTest.java
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
 *
 */
package pcgen.inttest.game_3e;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.inttest.PcgenFtlTestCase;

/**
 * Tests a 3e 2nd level Elf Cleric Law and Protection domains.
 */
@SuppressWarnings("nls")
public class pcGenGUICleElfTest extends PcgenFtlTestCase
{

	/**
	 * Run the test.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(pcGenGUICleElfTest.class);
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name No Idea.
	 */
	public pcGenGUICleElfTest(String name)
	{
		super(name);
	}

	/**
	 * Returns a test suite of all the tests in this class.
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUICleElfTest.class);
	}

	/**
	 * Load and output the character.
	 * 
	 * @throws Exception If there is a problem.
	 */
	public void testCleElf() throws Exception
	{
		runTest("3e_CleElf", "3e");
	}
}

/*
 * pcGenGUIMaloryTest.java
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
 * Created on 09/07/2015
 *
 * $Id$
 */
package pcgen.inttest.game_35e;

import pcgen.inttest.PcgenFtlTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * See PCG file for details. 
 */
@SuppressWarnings("nls")
public class pcGenGUIMalloryTest extends PcgenFtlTestCase
{

	/**
	 *
	 */
	public pcGenGUIMalloryTest()
	{
		super("35e_mallory");
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name
	 */

	public pcGenGUIMalloryTest(String name)
	{
		super(name);
	}

	/**
	 * Return a suite containing all the tests in this class.
	 * 
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIMalloryTest.class);
	}

	/**
	 * Load and output the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testMallory() throws Exception
	{
		runTest("35e_test_Mallory", "35e");
	}
}
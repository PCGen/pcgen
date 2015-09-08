/*
 * pcGenGUICloudGiantTest.java
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
 * Tests a 3.5e Cloud Giant with a Half-Dragon (Brass) template applied.
 * See it's PCG file for what it contains.  
 */
@SuppressWarnings("nls")
public class pcGenGUICloudGiantTest extends PcgenFtlTestCase
{

	/**
	 *
	 */
	public pcGenGUICloudGiantTest()
	{
		super("35e_cloudgiant");
	}

	/**
	 * standard JUnit style constructor
	 * @param name
	 */
	public pcGenGUICloudGiantTest(String name)
	{
		super(name);
	}

	/**
	 * return the test suite for this test
	 * @return the test suite for this test
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUICloudGiantTest.class);
	}

	/**
	 * Run the test
	 * @throws Exception
	 */
	public void testCloudGiantHalfDragon() throws Exception
	{
		runTest("CloudGiantHalfDragon", "35e");
	}

}
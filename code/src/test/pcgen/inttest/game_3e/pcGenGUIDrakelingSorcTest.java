/*
 * pcGenGUIDrakelingSorcTest.java
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
 * Created/Reinstated on 11/21/2015
 *
 * $Id$
 */
package pcgen.inttest.game_3e;

import pcgen.inttest.pcGenGUITestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a 3e 4th level Human Sorcerer with a Drakling template applied.
 * See PCG file for details.
 */
@SuppressWarnings("nls")
public class pcGenGUIDrakelingSorcTest extends pcGenGUITestCase
{
	/**
	 * Runs the test case.
	 * @param args
	 */
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(pcGenGUIDrakelingSorcTest.class);
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name No idea.
	 */
	public pcGenGUIDrakelingSorcTest(String name)
	{
		super(name);
	}

	/**
	 * Returns a test suite 
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIDrakelingSorcTest.class);
	}

	/**
	 * Load and output the character.
	 * @throws Exception If an error occurs.
	 */
	public void testDrakelingSorc() throws Exception
	{
		// Commented out as it fails due to removal of dependant sources.
		//runTest("3e_DrakelingSorc", "3e");
	}

}
/*
 * pcGenGUIIlyanaTest.java
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
 * Created/Reinstated on 09/07/2015
 *
 */
package pcgen.inttest.game_modern;

import pcgen.inttest.PcgenFtlTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests a Modern Tough Hero 3/Strong Hero 3.
 * See the PCG file for details
 */
@SuppressWarnings("nls")
public class pcGenGUIIlyanaTest extends PcgenFtlTestCase
{

	/**
	 * 
	 */
	public pcGenGUIIlyanaTest()
	{
		super("msrd_ilyana");
	}

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name
	 */
	public pcGenGUIIlyanaTest(String name)
	{
		super(name);
	}

	/**
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIIlyanaTest.class);
	}

	/**
	 * Loads and outputs the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testIlyana() throws Exception
	{
		runTest("msrd_Ilyana", "Modern");
	}
}

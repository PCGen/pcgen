/**
 * EquipmentUtilitiesTest.java
 * Copyright 2006 (C) Andrew Wilson <nuance@sourceforge.net>
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
 *
 * Created on 17 March 2005
 *
 * $Author: nuance $
 * $Date: 2006-03-22 00:25:03 +0000 (Wed, 22 Mar 2006) $
 * $Revision: 362 $
 */
package pcgen.core;

import pcgen.PCGenTestCase;

/**
 * @author nuance
 *
 */
public class EquipmentUtilitiesTest extends PCGenTestCase
{

	/**
	 * Run the test
	 * @param args don't need args apparently
	 */
	public static void main(final String[] args)
	{
		junit.textui.TestRunner.run(EquipmentUtilitiesTest.class);
	}

	/**
	 * Test method for 'pcgen.core.EquipmentUtilities.appendToName(String, String)'
	 */
	public void testAppendToName()
	{
		final String bare = "Bare Thing";
		final String decoration = "Mad cow";

		is(EquipmentUtilities.appendToName(bare, decoration),
			strEq("Bare Thing (Mad cow)"), "Choice appends to name correctly");
	}

}

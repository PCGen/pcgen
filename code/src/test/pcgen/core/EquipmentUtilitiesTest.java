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

import java.util.ArrayList;

import pcgen.PCGenTestCase;

/**
 * @author andrew
 *
 */
public class EquipmentUtilitiesTest extends PCGenTestCase {

	/*
	 * @see PCGenTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'pcgen.core.EquipmentUtilities.appendToName(String, String)'
	 */
	public void testAppendToName() {
		String bare = "Bare Thing";
		String decoration = "Mad cow";
		
		is(EquipmentUtilities.appendToName(bare, decoration), strEq("Bare Thing (Mad cow)"), "Choice appends to name correctly");		
	}

	/*
	 * Test method for 'pcgen.core.EquipmentUtilities.removeChoicesFromName(String)'
	 */
	public void testRemoveChoicesFromName() {
		is(EquipmentUtilities.removeChoicesFromName("Bare Thing (Mad cow)"), strEq("Bare Thing"), "Choice is removed from name correctly");		
	}

	/*
	 * Test method for 'pcgen.core.EquipmentUtilities.getUndecoratedName(String, ArrayList)'
	 */
	public void testGetUndecoratedName() {
		String name = "foo (bar, baz)";
		ArrayList specifics = new ArrayList();
		specifics.add("quxx");
		
		is(EquipmentUtilities.getUndecoratedName(name, specifics), strEq("foo"), "Got correct undecorated name");
		is(new Integer(specifics.size()), eq(2), "First extracted decoration is correct");
		is(specifics.get(0), strEq("bar"), "First extracted decoration is correct");
		is(specifics.get(1), strEq("baz"), "Second extracted decoration is correct");
	}

}

/**
 * AbilityStoreTest.java
 *
 * Copyright 2006 (C) Andrew Wilson <nuance@sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 22-Mar-2006
 *
 * Current Ver: $Revision: 201 $
 * Last Editor: $Author: nuance $
 * Last Edited: $Date: 2006-03-14 23:19:50 +0000 (Tue, 14 Mar 2006) $
 */
package pcgen.core;

import java.util.Iterator;

import pcgen.PCGenTestCase;

public class AbilityStoreTest extends PCGenTestCase {

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

	/**
	 * Test method for 'pcgen.core.AbilityStore.addAbilityInfo(String, String, String, boolean, boolean)'
	 */
	public void testAddAbilityInfo1() {
		AbilityStore abSt = new AbilityStore();
		String       abs  = "CATEGORY=FEAT|Abseil|Parachute|Plummet";
		
		abSt.addAbilityInfo(abs, "", "|", false, false);
		is(new Integer(abSt.size()), eq(3), "made 3 objects");

		Iterator it = abSt.getKeyIterator("FEAT");
		is(((AbilityInfo) it.next()).getKeyName(), strEq("Abseil"),    "First Ability is correct");
		is(((AbilityInfo) it.next()).getKeyName(), strEq("Parachute"), "Second Ability is correct");
		is(((AbilityInfo) it.next()).getKeyName(), strEq("Plummet"),   "Third Ability is correct");
	}

	/**
	 * Test method for 'pcgen.core.AbilityStore.addAbilityInfo(String, String, String, boolean, boolean)'
	 */
	public void testAddAbilityInfo2() {
		AbilityStore abSt = new AbilityStore();
		String       abs  = "CATEGORY=TALENT|Glide|Gird|CATEGORY=FEAT|Abseil|Parachute|Glide";
		
		abSt.addAbilityInfo(abs, "", "|", false, false);
		is(new Integer(abSt.size()), eq(5), "made 5 objects");

		Iterator it = abSt.getKeyIterator("FEAT");
		is(((AbilityInfo) it.next()).getKeyName(), strEq("Abseil"),    "First Ability is correct");
		is(((AbilityInfo) it.next()).getKeyName(), strEq("Glide"),     "Second Ability is correct");
		is(((AbilityInfo) it.next()).getKeyName(), strEq("Parachute"), "Third Ability is correct");

		it = abSt.getKeyIterator("TALENT");
		is(((AbilityInfo) it.next()).getKeyName(), strEq("Gird"),      "Fourth Ability is correct");
		is(((AbilityInfo) it.next()).getKeyName(), strEq("Glide"),     "Fifth  Ability is correct");
	}

	/**
	 * Test method for 'pcgen.core.AbilityStore.getParsableStringRepresentation()'
	 */
	public void testGetParsableStringRepresentation1() {
		AbilityStore abSt = new AbilityStore();
		String       abs  = "CATEGORY=FEAT|Abseil|Parachute|Plummet";
		
		abSt.addAbilityInfo(abs, "", "|", false, false);
		is(new Integer(abSt.size()), eq(3), "made 3 objects");

		is(abSt.getParsableStringRepresentation(), strEq(abs), "Got expected string generated");
	}

	/**
	 * Test method for 'pcgen.core.AbilityStore.getParsableStringRepresentation()'
	 */
	public void testGetParsableStringRepresentation2() {
		AbilityStore abSt   = new AbilityStore();
		String       abs    = "CATEGORY=TALENT|Glide|Gird|CATEGORY=FEAT|Abseil|Parachute|Glide";
		String       sorted = "CATEGORY=FEAT|Abseil|Glide|Parachute|CATEGORY=TALENT|Gird|Glide";
		
		abSt.addAbilityInfo(abs, "", "|", false, false);
		String roundTrip = abSt.getParsableStringRepresentation(); 

		is(new Integer(abSt.size()), eq(5), "made 5 objects");

		is(roundTrip, strEq(sorted), "Got expected string generated");

		abSt = new AbilityStore();
		
		abSt.addAbilityInfo(roundTrip, "", "|", false, false);
		is(new Integer(abSt.size()), eq(5), "made 5 objects");

		is(abSt.getParsableStringRepresentation(), strEq(sorted), "Got expected string generated");
	}
}

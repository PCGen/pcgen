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
import pcgen.util.TestHelper;

/**
 * Test class for AbilityStore
 */
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
		TestHelper.makeAbility("Abseil", "FEAT", "General.Fighter");
		TestHelper.makeAbility("Parachute", "FEAT", "General.Fighter");
		TestHelper.makeAbility("Plummet", "FEAT", "General.Fighter");

		AbilityStore abSt = new AbilityStore();
		String       abs  = "CATEGORY=FEAT|KEY_Abseil|KEY_Parachute|KEY_Plummet";

		abSt.addAbilityInfo(abs, "", "|", false);
		is(abSt.size(), eq(3), "made 3 objects");

		Iterator<Ability> it = abSt.getKeyIterator("FEAT");
		is(it.next().getKeyName(), strEq("KEY_Abseil"),    "First Ability is correct");
		is(it.next().getKeyName(), strEq("KEY_Parachute"), "Second Ability is correct");
		is(it.next().getKeyName(), strEq("KEY_Plummet"),   "Third Ability is correct");
	}

	/**
	 * Test method for 'pcgen.core.AbilityStore.addAbilityInfo(String, String, String, boolean, boolean)'
	 */
	public void testAddAbilityInfo2() {
		TestHelper.makeAbility("Glide", "TALENT", "General.Fighter");
		TestHelper.makeAbility("Gird", "TALENT", "General.Fighter");
		TestHelper.makeAbility("Abseil", "FEAT", "General.Fighter");
		TestHelper.makeAbility("Parachute", "FEAT", "General.Fighter");
		TestHelper.makeAbility("Glide", "FEAT", "General.Fighter");
		AbilityStore abSt = new AbilityStore();
		String       abs  = "CATEGORY=TALENT|KEY_Glide|KEY_Gird|CATEGORY=FEAT|KEY_Abseil|KEY_Parachute|KEY_Glide";

		abSt.addAbilityInfo(abs, "", "|", false);
		is(abSt.size(), eq(5), "made 5 objects");

		Iterator<Ability> it = abSt.getKeyIterator("FEAT");
		is(it.next().getKeyName(), strEq("KEY_Abseil"),    "First Ability is correct");
		is(it.next().getKeyName(), strEq("KEY_Glide"),     "Second Ability is correct");
		is(it.next().getKeyName(), strEq("KEY_Parachute"), "Third Ability is correct");

		it = abSt.getKeyIterator("TALENT");
		is(it.next().getKeyName(), strEq("KEY_Gird"),      "Fourth Ability is correct");
		is(it.next().getKeyName(), strEq("KEY_Glide"),     "Fifth  Ability is correct");
	}

	/**
	 * Test method for 'pcgen.core.AbilityStore.getParsableStringRepresentation()'
	 */
	public void testGetParsableStringRepresentation1() {
		TestHelper.makeAbility("Abseil", "FEAT", "General.Fighter");
		TestHelper.makeAbility("Parachute", "FEAT", "General.Fighter");
		TestHelper.makeAbility("Plummet", "FEAT", "General.Fighter");
		AbilityStore abSt = new AbilityStore();
		String       abs  = "CATEGORY=FEAT|KEY_Abseil|KEY_Parachute|KEY_Plummet";

		abSt.addAbilityInfo(abs, "", "|", false);
		is(abSt.size(), eq(3), "made 3 objects");

		is(abSt.getParsableStringRepresentation(), strEq(abs), "Got expected string generated");
	}

	/**
	 * Test method for 'pcgen.core.AbilityStore.getParsableStringRepresentation()'
	 */
	public void testGetParsableStringRepresentation2() {
		TestHelper.makeAbility("Glide", "TALENT", "General.Fighter");
		TestHelper.makeAbility("Gird", "TALENT", "General.Fighter");
		TestHelper.makeAbility("Abseil", "FEAT", "General.Fighter");
		TestHelper.makeAbility("Parachute", "FEAT", "General.Fighter");
		TestHelper.makeAbility("Glide", "FEAT", "General.Fighter");
		AbilityStore abSt   = new AbilityStore();
		String       abs    = "CATEGORY=TALENT|KEY_Glide|KEY_Gird|CATEGORY=FEAT|KEY_Abseil|KEY_Parachute|KEY_Glide";
		String       sorted = "CATEGORY=FEAT|KEY_Abseil|KEY_Glide|KEY_Parachute|CATEGORY=TALENT|KEY_Gird|KEY_Glide";

		abSt.addAbilityInfo(abs, "", "|", false);
		String roundTrip = abSt.getParsableStringRepresentation();

		is(abSt.size(), eq(5), "made 5 objects");

		is(roundTrip, strEq(sorted), "Got expected string generated");

		abSt = new AbilityStore();

		abSt.addAbilityInfo(roundTrip, "", "|", false);
		is(abSt.size(), eq(5), "made 5 objects");

		is(abSt.getParsableStringRepresentation(), strEq(sorted), "Got expected string generated");
	}
}

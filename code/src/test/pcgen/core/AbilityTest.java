/**
 * AbilityTest.java
 * Copyright 2006 (C) Andrew Wilson <nuance@sourceforge.net>
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
 * $Revision$
 * $Date$
 * $Time$
 * 
 * $id$
 */
package pcgen.core;

import pcgen.PCGenTestCase;
import pcgen.util.TestHelper;

/**
 * Class to test ABILITY
 */
public class AbilityTest extends PCGenTestCase {

	/**
	 * Constructor for AbilityTest.
	 * @param arg0
	 */
	public AbilityTest(String arg0) {
		super(arg0);
	}

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
	 * Test method for 'pcgen.core.Ability.isSameBaseAbility(Ability)'
	 */
	public final void testIsSameBaseAbility() {
		Ability ab1 = TestHelper.makeAbility("Perform (Dance)", "FEAT", "General.Fighter");
		ab1.setMultiples("NO");

		Ability ab2 = TestHelper.makeAbility("Perform (Dance)", "BARDIC", "General.Bardic");
		ab2.setMultiples("NO");		
		
		Ability ab3 = TestHelper.makeAbility("Perform (Oratory)", "FEAT", "General.Fighter");
		ab3.setMultiples("NO");

		Ability ab4 = TestHelper.makeAbility("Perform", "FEAT", "General.Fighter");
		ab3.setMultiples("NO");

		Ability ab5 = TestHelper.makeAbility("Weapon Throwing", "FEAT", "General.Fighter");
		ab5.setMultiples("YES");
		ab5.addAssociated("Wellies");
		
		Ability ab6 = (Ability) ab5.clone();
		ab6.addAssociated("Bar Stool");

		Ability ab7 = (Ability) ab5.clone();
		ab7.addAssociated("Tantrum");
		ab7.setName("Weapon Throwing (Tantrum)");
		
		is (ab1.getName(), strEq("Perform (Dance)"),           "Ability1 name is correct");
		is (ab2.getName(), strEq("Perform (Dance)"),           "Ability2 name is correct");
		is (ab3.getName(), strEq("Perform (Oratory)"),         "Ability3 name is correct");
		is (ab4.getName(), strEq("Perform"),                   "Ability4 name is correct");		
		is (ab5.getName(), strEq("Weapon Throwing"),           "Ability5 name is correct");
		is (ab6.getName(), strEq("Weapon Throwing"),           "Ability6 name is correct");
		is (ab7.getName(), strEq("Weapon Throwing (Tantrum)"), "Ability7 name is correct");

		is (new Boolean(ab1.isSameBaseAbility(ab2)), eq(false), "Abilities 1 & 2 are not the same");
		is (new Boolean(ab1.isSameBaseAbility(ab3)), eq(false), "Abilities 1 & 3 are not the same");	
		is (new Boolean(ab1.isSameBaseAbility(ab4)), eq(false), "Abilities 1 & 4 are not the same");	
		is (new Boolean(ab5.isSameBaseAbility(ab6)), eq(true),  "Abilities 5 & 6 are the same");	
		is (new Boolean(ab5.isSameBaseAbility(ab7)), eq(true),  "Abilities 5 & 7 are the same");	
	}
}

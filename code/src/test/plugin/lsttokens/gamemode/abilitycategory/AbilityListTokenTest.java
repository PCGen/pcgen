/*
 * AbilityListTokenTest.java
 * Copyright 2008 (C) James Dempsey
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
 * Created on 26/12/2008 9:55:57 AM
 *
 * $Id: $
 */

package plugin.lsttokens.gamemode.abilitycategory;

import pcgen.PCGenTestCase;
import pcgen.core.AbilityCategory;


/**
 * The Class <code>AbilityListTokenTest</code> verifies the processing of the 
 * AbilityListToken.
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
public class AbilityListTokenTest extends PCGenTestCase
{

	/**
	 * Test a single entry is parsed correctly
	 */
	public void testSingleEntry()
	{
		AbilityCategory aCat = new AbilityCategory("TestCat");
		assertEquals("Test category should start with an empty list of keys",
			0, aCat.getAbilityKeys().size());

		AbilityListToken token = new AbilityListToken();
		token.parse(aCat, "Track");
		assertEquals("Test category should now have 1 key", 1, aCat
			.getAbilityKeys().size());
		assertTrue("Track should be in the list now", aCat.getAbilityKeys()
			.contains("Track"));
	}
	
	/**
	 * Test that multiple entries are parsed correctly.
	 */
	public void testMultipleEntries()
	{
		AbilityCategory aCat = new AbilityCategory("TestCat");
		assertEquals("Test category should start with an empty list of keys",
			0, aCat.getAbilityKeys().size());

		AbilityListToken token = new AbilityListToken();
		token.parse(aCat, "Track|Point Blank Shot");
		assertEquals("Test category should now have 2 keys", 2, aCat
			.getAbilityKeys().size());
		assertTrue("Track should be in the list now", aCat.getAbilityKeys()
			.contains("Track"));
		assertTrue("Point Blank Shot should be in the list now", aCat.getAbilityKeys()
			.contains("Point Blank Shot"));
		assertFalse("Power Attack should not be in the list", aCat.getAbilityKeys()
			.contains("Power Attack"));
	}
	
	/**
	 * Test that entries with associated choices are parsed correctly
	 */
	public void testEntriesWithAssoc()
	{
		AbilityCategory aCat = new AbilityCategory("TestCat");
		assertEquals("Test category should start with an empty list of keys",
			0, aCat.getAbilityKeys().size());

		AbilityListToken token = new AbilityListToken();
		token.parse(aCat, "Point Blank Shot|Skill Focus (Ride)|Skill Focus (Bluff)");
		assertEquals("Test category should now have 3 keys", 3, aCat
			.getAbilityKeys().size());
		assertTrue("Point Blank Shot should be in the list now", aCat.getAbilityKeys()
			.contains("Point Blank Shot"));
		assertTrue("Appraise should be in the list now", aCat.getAbilityKeys()
			.contains("Skill Focus (Ride)"));
		assertTrue("Appraise should be in the list now", aCat.getAbilityKeys()
			.contains("Skill Focus (Bluff)"));
		
	}
}

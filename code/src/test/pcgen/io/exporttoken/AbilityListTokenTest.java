/*
 * AbilityListTokenTest.java
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 *
 *
 */
package pcgen.io.exporttoken;

import java.util.List;
import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;
import pcgen.util.TestHelper;
import pcgen.util.enumeration.Visibility;

/**
 * <code>AbilityListTokenTest</code> tests the functioning of the ABILITYLIST 
 * token processing code. 
 *
 *
 */
public class AbilityListTokenTest extends AbstractCharacterTestCase
{

	/**
	 * Quick test suite creation - adds all methods beginning with "test"
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(AbilityListTokenTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
    @Override
	protected void setUp() throws Exception
	{
		super.setUp();
		// Make some ability categories and add them to the game mode
		AbilityCategory bardCategory = Globals.getContext().getReferenceContext()
				.constructNowIfNecessary(AbilityCategory.class, "BARDIC");

		Ability ab1 = TestHelper.makeAbility("Perform (Dance)", AbilityCategory.FEAT, "General.Fighter");
		ab1.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		ab1.put(ObjectKey.VISIBILITY, Visibility.DEFAULT);
		addAbility(AbilityCategory.FEAT, ab1);

		Ability ab2 = TestHelper.makeAbility("Perform (Dance)", "BARDIC", "General.Bardic");
		ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		addAbility(bardCategory, ab2);

		Ability ab3 = TestHelper.makeAbility("Perform (Oratory)", AbilityCategory.FEAT, "General.Fighter");
		ab3.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		addAbility(AbilityCategory.FEAT, ab3);

		Ability ab4 = TestHelper.makeAbility("Silent Step", AbilityCategory.FEAT, "General");
		ab4.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		addAbility(AbilityCategory.FEAT, ab4);
	}

	/**
	 * Test the output for positive numbers with fractions.
	 */
	public void testTypes()
	{
		AbilityListToken tok = new AbilityListToken();
		ExportHandler eh = new ExportHandler(null);
		PlayerCharacter character = getCharacter();

		is(tok.getToken("ABILITYLIST.FEAT", character, eh), 
				strEq("Perform (Dance), Perform (Oratory), Silent Step"), "ABILITYLIST.FEAT");

		is(tok.getToken("ABILITYLIST.FEAT.TYPE=Fighter", character, eh),
				strEq("Perform (Dance), Perform (Oratory)"), "ABILITYLIST.FEAT.TYPE=Fighter");

		is(tok.getToken("ABILITYLIST.FEAT.!TYPE=Fighter", character, eh),
				strEq("Silent Step"), "ABILITYLIST.FEAT.!TYPE=Fighter");
	}

	/**
	 * Test the output for negative numbers with fractions.
	 */
	public void testCategory()
	{
		AbilityListToken tok = new AbilityListToken();
		ExportHandler eh = new ExportHandler(null);
		PlayerCharacter character = getCharacter();

		is(tok.getToken("ABILITYLIST.BARDIC", character, eh),
			strEq("Perform (Dance)"),
			"ABILITYLIST.BARDIC");
	}

	/**
	 * Test the JEP count function on abilities.  
	 */
	public void testCount()
	{
//		verbose = true;
		PlayerCharacter character = getCharacter();

		AbilityCategory featCategory = 
			SettingsHandler.getGame().getAbilityCategory("Feat");

		Ability ab5 = TestHelper.makeAbility("Silent Step (Greater)", AbilityCategory.FEAT, "General");
		ab5.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		ab5.put(ObjectKey.VISIBILITY, Visibility.OUTPUT_ONLY);
		addAbility(featCategory, ab5);

        Ability ab6 = TestHelper.makeAbility("Perform (Fiddle)", AbilityCategory.FEAT, "Bardic");
        ab6.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
        addAbility(featCategory, ab6);


        is(character
			.getVariableValue("count(\"ABILITIES\",\"CATEGORY=FEAT\",\"VISIBILITY=DEFAULT\")",""),
			eq(4.0, 0.1),
			"count(\"ABILITIES\",\"CATEGORY=FEAT\",\"VISIBILITY=DEFAULT\")");

		is(character
				.getVariableValue("count(\"ABILITIES\",\"CATEGORY=FEAT\",\"VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY\")",""),
				eq(5.0, 0.1),
				"count(\"ABILITIES\",\"CATEGORY=FEAT\",\"VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY\")");

		is(character
				.getVariableValue("count(\"ABILITIES\",\"CATEGORY=FEAT[and]TYPE=Fighter\",\"VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY\")",""),
				eq(2.0, 0.1),
				"count(\"ABILITIES\",\"CATEGORY=FEAT[and]TYPE=Fighter\",\"VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY\")");

        is(character
                .getVariableValue("count(\"ABILITIES\",\"CATEGORY=BARDIC[and]TYPE=Bardic.General\")", ""),
                eq(1.0, 0.1),
                "count(\"ABILITIES\",\"CATEGORY=BARDIC[and]TYPE=Bardic.General\")");

        is(character
				.getVariableValue("count(\"ABILITIES\",\"NATURE=AUTOMATIC\")",""),
				eq(0.0, 0.1),
				"count(\"ABILITIES\",\"NATURE=AUTOMATIC\")");

		is(character
				.getVariableValue("count(\"ABILITIES\",\"NATURE=VIRTUAL\")",""),
				eq(0.0, 0.1),
				"count(\"ABILITIES\",\"NATURE=VIRTUAL\")");

		is(character
				.getVariableValue("count(\"ABILITIES\",\"NATURE=NORMAL\")",""),
				eq(6.0, 0.1),
				"count(\"ABILITIES\",\"NATURE=NORMAL\")");

        is(character
                .getVariableValue("count(\"ABILITIES\")",""),
                eq(6.0, 0.1),
                "count(\"ABILITIES\")");
	}

	/**
	 * Test the mechanism of splitting FOR node parameters to
	 * ensure it copes with JEP functions with multiple comma 
	 * separated parameters. 
	 */
	public void testForNodeSplit()
	{
		String testStr =
				"|FOR,%feat,0,count(\"ABILITIES\",\"CATEGORY=FEAT\",\"VISIBILITY=VISIBLE\")-1,1,0|";

		List<String> result = ExportHandler.getParameters(testStr);
		assertEquals("Complex split len", 6, result.size());
		assertEquals("Complex split combined token 0", "|FOR", result.get(0));
		assertEquals("Complex split combined token 1", "%feat", result.get(1));
		assertEquals("Complex split combined token 2", "0", result.get(2));
		assertEquals("Complex split combined token 3",
			"count(\"ABILITIES\",\"CATEGORY=FEAT\",\"VISIBILITY=VISIBLE\")-1",
			result.get(3));
		assertEquals("Complex split combined token 4", "1", result.get(4));
		assertEquals("Complex split combined token 5", "0|", result.get(5));
	}

	public void testForNodeSplitNonJEP()
	{
		String testStr =
				"|FOR,%equip1,0,(COUNT[EQUIPMENT.MERGELOC.Not.Coin.NOT.Gem]-1)/2,1,0|";

		List<String> result = ExportHandler.getParameters(testStr);
		assertEquals("Complex split len", 6, result.size());
	}
}

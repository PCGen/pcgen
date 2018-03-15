/*
 * Copyright 2007 (C) andrew wilson <nuance@users.sourceforge.net>
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
 */
package plugin.jepcommands;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.ChronicleEntry;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.util.TestHelper;
import pcgen.util.enumeration.Visibility;
import plugin.lsttokens.testsupport.BuildUtilities;

/**
 * <code>CountCommandTest</code> tests the functioning of the jep count plugin
 */
public class CountCommandTest extends AbstractCharacterTestCase
{

	/**
	 * Quick test suite creation - adds all methods beginning with "test"
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(CountCommandTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
    @Override
	protected void setUp() throws Exception
	{
		super.setUp();
		final PlayerCharacter character = getCharacter();

		// Make some ability categories and add them to the game mode
		AbilityCategory bardCategory = Globals.getContext().getReferenceContext()
				.constructNowIfNecessary(AbilityCategory.class, "BARDIC");
		AbilityCategory clericalCategory = Globals.getContext().getReferenceContext()
				.constructNowIfNecessary(AbilityCategory.class, "CLERICAL");

        final Ability[] abArray = new Ability[14];

        abArray[0]  = TestHelper.makeAbility("Quick Draw",               BuildUtilities.getFeatCat(),   "General.Fighter");
        abArray[1]  = TestHelper.makeAbility("Improved Initiative",      BuildUtilities.getFeatCat(),   "General.Fighter");
        abArray[2]  = TestHelper.makeAbility("Silent Step",              BuildUtilities.getFeatCat(),   "General.Fighter.Rogue");
        abArray[3]  = TestHelper.makeAbility("Silent Step (Greater)",    BuildUtilities.getFeatCat(),   "General.Fighter.Rogue");

        abArray[4]  = TestHelper.makeAbility("Hidden 01",                BuildUtilities.getFeatCat(),   "ClassAbility");
        abArray[5]  = TestHelper.makeAbility("Perform (Dance)",          BuildUtilities.getFeatCat(),   "ClassAbility");

        abArray[6]  = TestHelper.makeAbility("Perform (Dance)",          "BARDIC", "Performance.SpecialAbility");
        abArray[7]  = TestHelper.makeAbility("Perform (Oratory)",        "BARDIC", "Performance.SpecialAbility");
        abArray[8]  = TestHelper.makeAbility("Perform (Fiddle)",         "BARDIC", "Performance.SpecialAbility");
        abArray[9]  = TestHelper.makeAbility("Perform (Bass)",           "BARDIC", "Performance.SpecialAbility");
        abArray[10] = TestHelper.makeAbility("Epic Performance (Dance)", "BARDIC", "Performance.ExtraordinaryAbility.Epic");
        abArray[11] = TestHelper.makeAbility("Epic Performance (Bass)",  "BARDIC", "Performance.ExtraordinaryAbility.Epic");
        abArray[12] = TestHelper.makeAbility("Turning",                  "CLERICAL", "SpecialAbility.");
        abArray[13] = TestHelper.makeAbility("Epic Turning",             "CLERICAL", "SpecialAbility.Epic");

        for (final Ability ab : abArray) {
            ab.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
        }

        abArray[3].put(ObjectKey.VISIBILITY, Visibility.DISPLAY_ONLY);

        abArray[4].put(ObjectKey.VISIBILITY, Visibility.HIDDEN);
        abArray[5].put(ObjectKey.VISIBILITY, Visibility.HIDDEN);

        abArray[10].put(ObjectKey.VISIBILITY, Visibility.OUTPUT_ONLY);
        abArray[11].put(ObjectKey.VISIBILITY, Visibility.OUTPUT_ONLY);
        abArray[13].put(ObjectKey.VISIBILITY, Visibility.OUTPUT_ONLY);



        abArray[1].put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		Globals.getContext().unconditionallyProcess(abArray[1], "CHOOSE", "STRING|one|two|three");
        AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), abArray[1], "one");
        AbstractCharacterTestCase.applyAbility(character, BuildUtilities.getFeatCat(), abArray[1], "two");

        addAbility(BuildUtilities.getFeatCat(), abArray[0]);
		for (int i = 2;6 > i;i++) {
            Ability anAbility = abArray[i];
            addAbility(BuildUtilities.getFeatCat(), anAbility);
        }

        for (int i = 6;12 > i;i++) {
            Ability anAbility = abArray[i];
            addAbility(bardCategory, anAbility);
        }

        for (int i = 12;14 > i;i++) {
            Ability anAbility = abArray[i];
            addAbility(clericalCategory, anAbility);
        }


    }


//    /* Test the cast where we only have the type os count, i.e. all Abilities */   
//    public void testCountAbilities01()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\")",""),
//           eq(15.0, 0.1),
//           "count(\"ABILITIES\")");
//    }
//
//    /* Test case 02 count all of the abilities of category FEAT */
//    public void testCountAbilities02()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"CATEGORY=FEAT\")",""),
//           eq(7.0, 0.1),
//           "count(\"ABILITIES\",\"CATEGORY=FEAT\")");
//    }
//
//    /* Test case 03 count all of the abilities of category BARDIC */
//    public void testCountAbilities03()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"CATEGORY=BARDIC\")",""),
//           eq(6.0, 0.1),
//           "count(\"ABILITIES\",\"CATEGORY=BARDIC\")");
//    }
//
//    /* Test case 04 count all of the abilities of category CLERICAL */
//    public void testCountAbilities04()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"CATEGORY=CLERICAL\")",""),
//           eq(2.0, 0.1),
//           "count(\"ABILITIES\",\"CATEGORY=CLERICAL\")");
//    }
//
//    /* Test case 05 count all of the abilities of category FEAT _and_ BARDIC (should be zero) */
//    public void testCountAbilities05()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"CATEGORY=FEAT\",\"CATEGORY=BARDIC\")",""),
//           eq(0.0, 0.1),
//           "count(\"ABILITIES\",\"CATEGORY=FEAT\",\"CATEGORY=BARDIC\")");
//    }
//
//    /* Test case 06 count all of the abilities of category FEAT _and_ BARDIC using the
//     * explicit [and], this should still be zero */
//    public void testCountAbilities06()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"CATEGORY=FEAT[and]CATEGORY=BARDIC\")",""),
//           eq(0.0, 0.1),
//           "count(\"ABILITIES\",\"CATEGORY=FEAT[and]CATEGORY=BARDIC\")");
//    }
//
//    /* Test case 07 count all of the abilities of category FEAT _or_ BARDIC */
//    public void testCountAbilities07()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"CATEGORY=FEAT[or]CATEGORY=BARDIC\")",""),
//           eq(13.0, 0.1),
//           "count(\"ABILITIES\",\"CATEGORY=FEAT[or]CATEGORY=BARDIC\")");
//    }
//
//
//    /* Test case 08 count all of the abilities of category FEAT _or_ BARDIC _or_ CLERICAL */
//    public void testCountAbilities08()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"CATEGORY=FEAT[or]CATEGORY=BARDIC[or]CATEGORY=CLERICAL\")",""),
//           eq(15.0, 0.1),
//           "count(\"ABILITIES\",\"CATEGORY=FEAT[or]CATEGORY=BARDIC[or]CATEGORY=CLERICAL\")");
//    }
//
//
//
//    /* Test case 09 count all of the abilities with visibility DEFAULT */
//    public void testCountAbilities09()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"VISIBILITY=DEFAULT\")",""),
//           eq(9.0, 0.1),
//           "count(\"ABILITIES\",\"VISIBILITY=DEFAULT\")");
//    }
//
//
//    /* Test case 10 count all of the abilities with visibility OUTPUT_ONLY */
//    public void testCountAbilities10()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"VISIBILITY=OUTPUT_ONLY\")",""),
//           eq(3.0, 0.1),
//           "count(\"ABILITIES\",\"VISIBILITY=OUTPUT_ONLY\")");
//    }
//
//    /* Test case 11 count all of the abilities with visibility DISPLAY_ONLY */
//    public void testCountAbilities11()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"VISIBILITY=DISPLAY_ONLY\")",""),
//           eq(1.0, 0.1),
//           "count(\"ABILITIES\",\"VISIBILITY=DISPLAY_ONLY\")");
//    }
//
//    /* Test case 12 count all of the abilities with visibility HIDDEN */
//    public void testCountAbilities12()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"VISIBILITY=HIDDEN\")",""),
//           eq(2.0, 0.1),
//           "count(\"ABILITIES\",\"VISIBILITY=HIDDEN\")");
//    }
//
//    /* Test case 13 count all the Abilities of category FEAT with visibility DEFAULT */
//    public void testCountAbilities13()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"CATEGORY=FEAT\",\"VISIBILITY=DEFAULT\")",""),
//            eq(4.0, 0.1),
//            "count(\"ABILITIES\",\"CATEGORY=FEAT\",\"VISIBILITY=DEFAULT\")");
//    }
//
//    /* Test case 14 count all the Abilities in Categories FEAT or CLERICAL with visibility DEFAULT or OUTPUT_ONLY  */
//    public void testCountAbilities14()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"CATEGORY=FEAT[or]CATEGORY=CLERICAL\",\"VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY\")",""),
//           eq(6.0, 0.1),
//           "count(\"ABILITIES\",\"CATEGORY=FEAT[or]CATEGORY=CLERICAL\",\"VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY\")");
//    }
//
//
//    /*****************
//     * TYPE Tests
//     */
//
//
//	public void testCountAbilities15()
//	{
//		final PlayerCharacter character = getCharacter();
//
//		is(character.getVariableValue("count(\"ABILITIES\",\"TYPE=Fighter\")",""),
//		   eq(5.0, 0.1),
//		   "count(\"ABILITIES\",\"TYPE=Fighter\")");
//    }
//
//	public void testCountAbilities16()
//	{
//		final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"TYPE=General.Fighter\")", ""),
//           eq(5.0, 0.1),
//           "count(\"ABILITIES\",\"TYPE=General.Fighter\")");
//    }
//
//    public void testCountAbilities17()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"TYPE=General.Fighter[or]TYPE=Epic\")", ""),
//           eq(8.0, 0.1),
//           "count(\"ABILITIES\",\"TYPE=General.Fighter[or]TYPE=Epic\")");
//    }
//
//
//    public void testCountAbilities18()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"(TYPE=Performance[or]TYPE=Epic)[and]TYPE=SpecialAbility\")", ""),
//           eq(5.0, 0.1),
//           "count(\"ABILITIES\",\"(TYPE=Performance[or]TYPE=Epic)[and]TYPE=SpecialAbility\")");
//    }
//
//    public void testCountAbilities19()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"TYPE=Performance[or](TYPE=Epic[and]TYPE=SpecialAbility)\")", ""),
//           eq(7.0, 0.1),
//           "count(\"ABILITIES\",\"TYPE=Performance[or](TYPE=Epic[and]TYPE=SpecialAbility)\")");
//    }
//
//    public void testCountAbilities20()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"TYPE=Performance[or](TYPE=Epic.SpecialAbility)\")", ""),
//           eq(7.0, 0.1),
//           "count(\"ABILITIES\",\"TYPE=Performance[or](TYPE=Epic.SpecialAbility)\")");
//    }
//
//    /*****************
//     * NATURE Tests
//     */
//
//    public void testCountAbilities21()
//	{
//		final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"NATURE=AUTOMATIC\")",""),
//		   eq(0.0, 0.1),
//		   "count(\"ABILITIES\",\"NATURE=AUTOMATIC\")");
//    }
//
//	public void testCountAbilities22()
//	{
//		final PlayerCharacter character = getCharacter();
//
//    	is(character.getVariableValue("count(\"ABILITIES\",\"NATURE=VIRTUAL\")",""),
//		   eq(0.0, 0.1),
//		   "count(\"ABILITIES\",\"NATURE=VIRTUAL\")");
//    }
//
//	public void testCountAbilities23()
//	{
//		final PlayerCharacter character = getCharacter();
//
//		is(character.getVariableValue("count(\"ABILITIES\",\"NATURE=NORMAL\")",""),
//		   eq(15.0, 0.1),
//		   "count(\"ABILITIES\",\"NATURE=NORMAL\")");
//    }
//
//
//    /*****************
//     * NAME Tests
//     */
//
//    public void testCountAbilities24()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"NAME=Dance Monkey boy dance\")",""),
//           eq(0.0, 0.1),
//           "count(\"ABILITIES\",\"NAME=Dance Monkey boy dance\")");
//    }
//
//    public void testCountAbilities25()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"NAME=Improved Initiative\")",""),
//           eq(2.0, 0.1),
//           "count(\"ABILITIES\",\"NAME=Improved Initiative\")");
//    }
//
//    public void testCountAbilities26()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"NAME=Perform (Dance)\")",""),
//           eq(2.0, 0.1),
//           "count(\"ABILITIES\",\"NAME=Perform (Dance)\")");
//    }
//
//    public void testCountAbilities27()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"NAME=Epic Performance (Dance)\")",""),
//           eq(1.0, 0.1),
//           "count(\"ABILITIES\",\"NAME=Epic Performance (Dance)\")");
//    }
//
//    public void testCountAbilities28()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        is(character.getVariableValue("count(\"ABILITIES\",\"NAME=Skill Focus (Profession (Basket weaving))[or]CATEGORY=FEAT\")",""),
//           eq(7.0, 0.1),
//           "count(\"ABILITIES\",\"NAME=Skill Focus (Profession (Basket weaving))[or]CATEGORY=FEAT\")");
//    }
//
//
//    public void testCountAbilities29()
//    {
//        final PlayerCharacter character = getCharacter();
//
//        final StringBuilder sB = new StringBuilder(100);
//
//        sB.append("count(\"ABILITIES\",");
//        sB.append("\"NAME=Turning");
//        sB.append("[or](((TYPE=Fighter)[and](TYPE=General))[and](NATURE=NORMAL))\")");
//
//        final String s = sB.toString();
//
//        is(character.getVariableValue(s,""), eq(6.0, 0.1), s);
//    }

	public void testCountAbilitiesByName()
	{
		final PlayerCharacter character = getCharacter();

		AbilityCategory gCat = Globals.getContext().getReferenceContext()
				.constructNowIfNecessary(AbilityCategory.class, "CLERICAL");

		final Ability ab =
				TestHelper.makeAbility("Eat Burger", "CLERICAL", "Clerical.General");

		ab.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		Globals.getContext().unconditionallyProcess(ab, "CHOOSE", "STRING|munch|devour|nibble|ignore");
		
		// now the tests

		final StringBuilder sB = new StringBuilder(100);

		sB.append("count(\"ABILITIES\",");
		sB.append("\"NAME=Eat Burger\")");

		final String s = sB.toString();

		is(character.getVariableValue(s,""), eq(0.0, 0.1), s + " no choices");
		
		AbilityCategory category = gCat;
		finalizeTest(ab, "munch", character, category);

		is(character.getVariableValue(s,""), eq(1.0, 0.1), s + " one choice");

		finalizeTest(ab, "devour", character, category);
		finalizeTest(ab, "nibble", character, category);
		assertEquals(3, character.getConsolidatedAssociationList(ab).size());
		character.setDirty(true);

		is(character.getVariableValue(s,""), eq(3.0, 0.1), s + " three choices");
	}

	public void testCountAbilitiesByKey()
	{
		final PlayerCharacter character = getCharacter();

		AbilityCategory gCat = Globals.getContext().getReferenceContext()
				.constructNowIfNecessary(AbilityCategory.class, "CLERICAL");

		final Ability ab =
				TestHelper.makeAbility("Eat Burger", "CLERICAL", "Clerical.General");

		ab.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		Globals.getContext().unconditionallyProcess(ab, "CHOOSE", "STRING|munch|devour|nibble|ignore");
		
		// now the tests

		final StringBuilder sB = new StringBuilder(100);

		sB.append("count(\"ABILITIES\",");
		sB.append("\"KEY=KEY_Eat Burger\")");

		final String countByKey = sB.toString();

		is(character.getVariableValue(countByKey,""), eq(0.0, 0.1), countByKey + " no choices");
		
		AbstractCharacterTestCase.applyAbility(character, gCat, ab, "munch");

		is(character.getVariableValue(countByKey,""), eq(1.0, 0.1), countByKey + " one choice");

		AbstractCharacterTestCase.applyAbility(character, gCat, ab, "devour");
		character.setDirty(true);
		
		is(character.getVariableValue(countByKey,""), eq(2.0, 0.1), countByKey + " two choices");

		AbstractCharacterTestCase.applyAbility(character, gCat, ab, "nibble");
		character.setDirty(true);

		is(character.getVariableValue(countByKey,""), eq(3.0, 0.1), countByKey + " three choices");
		
		String countKeyChoice = "count(\"ABILITIES\",\"KEY=KEY_Eat Burger(munch)\")";
		is(character.getVariableValue(countKeyChoice,""), eq(1.0, 0.1), countKeyChoice + " chosen");
		
		String countStr = "count(\"ABILITIES\",\"KEY=KEY_Turning\")";
		is(character.getVariableValue(countStr,""), eq(1.0, 0.1), countStr + " single application");
		
	}

	/**
	 * Verify counting CAMPAIGNHISTORY entries.
	 */
	public void testCountCampaignHistory()
	{
		final PlayerCharacter character = getCharacter();
		String countDefault = "count(\"CAMPAIGNHISTORY\")";
		String countVisible = "count(\"CAMPAIGNHISTORY\",\"EXPORT=YES\")";
		String countHidden = "count(\"CAMPAIGNHISTORY\",\"EXPORT=NO\")";
		String countAll = "count(\"CAMPAIGNHISTORY\",\"EXPORT=NO[or]EXPORT=YES\")";
		
		// No entries yet
		is(character.getVariableValue(countDefault,""), eq(0.0, 0.1), countDefault + " no choices");
		is(character.getVariableValue(countVisible,""), eq(0.0, 0.1), countVisible + " no choices");
		is(character.getVariableValue(countHidden,""), eq(0.0, 0.1), countHidden + " no choices");
		is(character.getVariableValue(countAll,""), eq(0.0, 0.1), countAll + " no choices");

		ChronicleEntry hiddenEntry =
				TestHelper.buildChronicleEntry(false, "Campaign", "Date", "GM",
					"Party", "Adventure", 1390, "Chronicle");
		character.addChronicleEntry(hiddenEntry);
		character.setDirty(true);
		is(character.getVariableValue(countDefault,""), eq(0.0, 0.1), countDefault + " one hidden");
		is(character.getVariableValue(countVisible,""), eq(0.0, 0.1), countVisible + " one hidden");
		is(character.getVariableValue(countHidden,""), eq(1.0, 0.1), countHidden + " one hidden");
		is(character.getVariableValue(countAll,""), eq(1.0, 0.1), countAll + " one hidden");

		ChronicleEntry visibleEntry =
				TestHelper.buildChronicleEntry(true, "Campaign", "Date2", "GM",
					"Party", "Adventure2", 1390, "Chronicle2");
		character.addChronicleEntry(visibleEntry);
		character.setDirty(true);
		is(character.getVariableValue(countDefault,""), eq(1.0, 0.1), countDefault + " one hidden, one visible");
		is(character.getVariableValue(countVisible,""), eq(1.0, 0.1), countVisible + " one hidden, one visible");
		is(character.getVariableValue(countHidden,""), eq(1.0, 0.1), countHidden + " one hidden, one visible");
		is(character.getVariableValue(countAll,""), eq(2.0, 0.1), countAll + " one hidden, one visible");

		ChronicleEntry thirdEntry =
				TestHelper.buildChronicleEntry(true, "Campaign", "Date3", "GM",
					"Party", "Adventure3", 1390, "Chronicle2");
		character.addChronicleEntry(thirdEntry);
		character.setDirty(true);
		is(character.getVariableValue(countDefault,""), eq(2.0, 0.1), countDefault + " one hidden, two visible");
		is(character.getVariableValue(countVisible,""), eq(2.0, 0.1), countVisible + " one hidden, two visible");
		is(character.getVariableValue(countHidden,""), eq(1.0, 0.1), countHidden + " one hidden, two visible");
		is(character.getVariableValue(countAll,""), eq(3.0, 0.1), countAll + " one hidden, two visible");
		
	}

}

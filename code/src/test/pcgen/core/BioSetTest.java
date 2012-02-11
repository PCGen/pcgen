/*
 * BioSetTest.java
 *
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on 04-Feb-2004
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.enumeration.StringKey;
import pcgen.persistence.lst.BioSetLoaderTest;

/**
 * A collection of tests to validate the functioning of the core BioSet class.
 *
 */
@SuppressWarnings("nls")
public class BioSetTest extends AbstractCharacterTestCase
{
	static final String[] BIO_SET_DATA =
			new String[]{
				"AGESET:0|Adulthood",
				"RACENAME:Human%		CLASS:Barbarian,Rogue,Sorcerer[BASEAGEADD:1d4]|Bard,Fighter,Paladin,Ranger[BASEAGEADD:1d6]|Cleric,Druid,Monk,Wizard[BASEAGEADD:2d6]",
				"RACENAME:Human%		SEX:Male[BASEHT:58|HTDIEROLL:2d10|BASEWT:120|WTDIEROLL:2d4|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]Female[BASEHT:53|HTDIEROLL:2d10|BASEWT:85|WTDIEROLL:2d4|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]",
				"RACENAME:Human%		BASEAGE:15	MAXAGE:34	AGEDIEROLL:5d4	HAIR:Blond|Brown	EYES:Blue	SKINTONE:Tanned|Pasty",
				"AGESET:1|Middle Age	BONUS:STAT|STR,CON,DEX|-1	BONUS:STAT|INT,WIS,CHA|1",
				"RACENAME:Human%		BASEAGE:35	MAXAGE:52	AGEDIEROLL:3d6",
				"AGESET:2|Old		BONUS:STAT|STR,CON,DEX|-3	BONUS:STAT|INT,WIS,CHA|2",
				"RACENAME:Human%		BASEAGE:53	MAXAGE:69	AGEDIEROLL:4d4+1",
				"AGESET:3|Venerable	BONUS:STAT|STR,CON,DEX|-6	BONUS:STAT|INT,WIS,CHA|3",
				"RACENAME:Human%		BASEAGE:70	MAXAGE:110	AGEDIEROLL:4d10"};

	/**
	 * Run the tests standalone from the command line.
	 * @param args Command line args - ignored.
	 */
	public static void main(final String[] args)
	{
		junit.textui.TestRunner.run(BioSetTest.class);
	}

	/**
	 * Quick test suite creation - adds all methods beginning with "test".
	 *
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(BioSetTest.class);
	}

	/**
	 * Basic constructor, name only.
	 * @param name The name of the test class.
	 */
	public BioSetTest(final String name)
	{
		super(name);
	}

	protected void additionalSetUp() throws Exception
	{
		BioSetLoaderTest.loadBioSet(Globals.getContext(), BIO_SET_DATA);
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		Globals.getBioSet().clearUserMap();

		super.tearDown();
	}

	/**
	 * Verify that the copyRaceTags function in BioSet
	 * is functioning properly.
	 */
	public void testCopyRaceTags()
	{
		final String BASE_RACE_NAME = "Human";
		final String NEW_RACE_NAME = "TestHuman";
		final String[] TEST_TAGS =
				new String[]{"HAIR", "EYES", "SKINTONE", "AGEDIEROLL", "CLASS",
					"BASEAGE", "MAXAGE", "SEX", "CLASS"};

		final BioSet currBioSet = Globals.getBioSet();

		currBioSet.copyRaceTags(Constants.NONE, BASE_RACE_NAME,
			Constants.NONE, NEW_RACE_NAME);

		List<String> baseRaceTag;
		List<String> newRaceTag;
		for (int i = 0; i < TEST_TAGS.length; i++)
		{
			final String testArg = TEST_TAGS[i];
			baseRaceTag =
					currBioSet.getTagForRace(Constants.NONE, BASE_RACE_NAME,
						testArg);
			newRaceTag =
					currBioSet.getTagForRace(Constants.NONE, NEW_RACE_NAME,
						testArg);
			// System.out.println(
			// "Got '"
			// + testArg
			// + "' base of "
			// + baseRaceTag
			// + " and new of "
			// + newRaceTag
			// + ".");
			for (Iterator<String> newIter = newRaceTag.iterator(), baseIter =
					baseRaceTag.iterator(); newIter.hasNext()
				&& baseIter.hasNext();)
			{
				final Object baseElem = baseIter.next();
				final Object newElem = newIter.next();
				assertEquals("Comparison of " + testArg + " values (b,n).",
					baseElem, newElem);
			}
		}

		//		System.out.println(
		//			currBioSet.getRacePCCText(Constants.NONE, NEW_RACE_NAME));
	}

	/**
	 * Verify that the randomize function in BioSet
	 * is functioning properly.
	 */
	public void testRandomize()
	{
		final int[] BASE_AGE = new int[]{15, 35, 53, 70};
		final int[] MAX_AGE = new int[]{34, 52, 69, 110};

		final BioSet currBioSet = Globals.getBioSet();
		final PlayerCharacter pc = getCharacter();
		final Race human = new Race();
		human.setName("NAME_Human");
		human.put(StringKey.KEY_NAME, "Human");
		pc.setRace(human);
		for (int ageCat = 0; ageCat < MAX_AGE.length; ageCat++)
		{
			currBioSet.randomize("AGECAT" + ageCat, pc);
			final int age = pc.getAge();
			//System.out.println("Age for cat " + ageCat + " is " + age + ".");
			assertTrue("Generated age " + age + " is not between "
				+ BASE_AGE[ageCat] + " and " + MAX_AGE[ageCat],
				(age >= BASE_AGE[ageCat] && age <= MAX_AGE[ageCat]));
		}
		currBioSet.randomize("AGE.HT.WT.EYES.HAIR.SKIN", pc);
		assertTrue("Generated height " + pc.getHeight()
			+ " is not in required range.", (pc.getHeight() >= 58 && pc
			.getHeight() <= 78));
		assertTrue("Generated weight " + pc.getWeight()
			+ " is not in required range.", (pc.getWeight() >= 120 && pc
			.getWeight() <= 280));
		assertTrue("Generated eye colour " + pc.getSafeStringFor(StringKey.EYE_COLOR)
			+ " is not valid.", ("Blue".equals(pc.getSafeStringFor(StringKey.EYE_COLOR))));
		assertTrue("Generated hair colour " + pc.getSafeStringFor(StringKey.HAIR_COLOR)
			+ " is not valid.", ("Blond".equals(pc.getSafeStringFor(StringKey.HAIR_COLOR)) || "Brown"
			.equals(pc.getSafeStringFor(StringKey.HAIR_COLOR))));
		assertTrue("Generated skin colour " + pc.getSafeStringFor(StringKey.SKIN_COLOR)
			+ " is not valid.", ("Pasty".equals(pc.getSafeStringFor(StringKey.SKIN_COLOR)) || "Tanned"
			.equals(pc.getSafeStringFor(StringKey.SKIN_COLOR))));
	}

	/**
	 * Test the age set
	 */
	public void testAgeSet()
	{
		final PlayerCharacter pc = getCharacter();
		final Race human = new Race();
		human.setName("Human");
		pc.setRace(human);
		pc.setAge(12);
		int idx = pc.getAgeSetIndex();
		assertEquals("Ageset for " + pc.getAge() + ".", 0, idx);

		pc.setAge(17);
		idx = pc.getAgeSetIndex();
		assertEquals("Ageset for " + pc.getAge() + ".", 0, idx);

		pc.setAge(36);
		idx = pc.getAgeSetIndex();
		assertEquals("Ageset for " + pc.getAge() + ".", 1, idx);

		pc.setAge(54);
		idx = pc.getAgeSetIndex();
		assertEquals("Ageset for " + pc.getAge() + ".", 2, idx);

		pc.setAge(72);
		idx = pc.getAgeSetIndex();
		assertEquals("Ageset for " + pc.getAge() + ".", 3, idx);

		Globals.getBioSet().getAgeSet(Region.getConstant(pc.getRegionString()), idx);

	}
}

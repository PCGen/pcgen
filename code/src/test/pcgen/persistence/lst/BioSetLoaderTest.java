/*
 * BioSetLoaderTest.java
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
 *
 *
 *
 *
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import pcgen.cdom.base.Constants;
import pcgen.core.BioSet;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.rules.context.LoadContext;

/**
 * A collection of tests to validate the functioning of the BioSetLoader class.
 * Static methods are also made available should other test classes require
 * BioSet loading functions.
 */
public final class BioSetLoaderTest extends TestCase
{
	/**
	 * The sample Bio set data for testing.
	 */
	private final static String[] BIO_SET_DATA =
			new String[]{
				"AGESET:0|Adulthood",
				"RACENAME:Human%		CLASS:Barbarian,Rogue,Sorcerer[BASEAGEADD:1d4]|Bard,Fighter,Paladin,Ranger[BASEAGEADD:1d6]|Cleric,Druid,Monk,Wizard[BASEAGEADD:2d6]",
				"RACENAME:Human%		SEX:Male[BASEHT:58|HTDIEROLL:2d10|BASEWT:120|WTDIEROLL:2d4|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]Female[BASEHT:53|HTDIEROLL:2d10|BASEWT:85|WTDIEROLL:2d4|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]",
				"RACENAME:Human%		BASEAGE:15	MAXAGE:34	AGEDIEROLL:5d4	HAIR:Blond|Brown	EYES:Blue	SKINTONE:Tanned|Pasty",
				"RACENAME:Half-Elf%	CLASS:Barbarian,Rogue,Sorcerer[BASEAGEADD:1d6]|Bard,Fighter,Paladin,Ranger[BASEAGEADD:2d6]|Cleric,Druid,Monk,Wizard[BASEAGEADD:3d6]",
				"RACENAME:Half-Elf%	SEX:Male[BASEHT:55|HTDIEROLL:2d8|BASEWT:100|WTDIEROLL:2d4|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]Female[BASEHT:53|HTDIEROLL:2d8|BASEWT:80|WTDIEROLL:2d4|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]",
				"RACENAME:Half-Elf%	BASEAGE:20	MAXAGE:61	AGEDIEROLL:7d6	HAIR:White|Pale Yellow|Silver-White|Black|Blue|Golden Blonde|Copper|Brown	EYES:Violet|Silver|Pink|Pale Blue|Gold-flecked Green|Gold-flecked Blue|Green|Gold|Brown|Hazel	SKINTONE:Obsidian|Fair|Tanned|Pasty|Bronze|Dark Brown|Copper",
				"AGESET:1|Middle Age	BONUS:STAT|STR,CON,DEX|-1	BONUS:STAT|INT,WIS,CHA|1",
				"RACENAME:Human%		BASEAGE:35	MAXAGE:52	AGEDIEROLL:3d6",
				"RACENAME:Half-Elf%	BASEAGE:62	MAXAGE:92	AGEDIEROLL:5d6",
				"AGESET:2|Old		BONUS:STAT|STR,CON,DEX|-3	BONUS:STAT|INT,WIS,CHA|2",
				"RACENAME:Human%		BASEAGE:53	MAXAGE:69	AGEDIEROLL:4d4+1",
				"RACENAME:Half-Elf%	BASEAGE:93	MAXAGE:124	AGEDIEROLL:5d6+1",
				"AGESET:3|Venerable	BONUS:STAT|STR,CON,DEX|-6	BONUS:STAT|INT,WIS,CHA|3",
				"RACENAME:Human%		BASEAGE:70	MAXAGE:110	AGEDIEROLL:4d10",
				"RACENAME:Half-Elf%	BASEAGE:125	MAXAGE:185	AGEDIEROLL:6d10",
				"AGESET:0|Adulthood",
				"RACENAME:Dwarf%		CLASS:Barbarian,Rogue,Sorcerer[BASEAGEADD:3d6]|Bard,Fighter,Paladin,Ranger[BASEAGEADD:5d6]|Cleric,Druid,Monk,Wizard[BASEAGEADD:7d6]",
				"RACENAME:Dwarf%		SEX:Male[BASEHT:45|HTDIEROLL:2d4|BASEWT:130|WTDIEROLL:2d6|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]Female[BASEHT:43|HTDIEROLL:2d4|BASEWT:100|WTDIEROLL:2d6|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]",
				"RACENAME:Dwarf%		BASEAGE:40	MAXAGE:124	AGEDIEROLL:4d20+1d4	HAIR:Blond|Brown|Bald|Gray	EYES:Blue|Black|Brown|Gray	SKINTONE:Tanned|Pasty|Fair|Gray",
				"AGESET:1|Middle Age	BONUS:STAT|STR,CON,DEX|-1	BONUS:STAT|INT,WIS,CHA|1",
				"RACENAME:Dwarf%		BASEAGE:125	MAXAGE:187	AGEDIEROLL:(7d8+1d6)",
				"AGESET:2|Old		BONUS:STAT|STR,CON,DEX|-3	BONUS:STAT|INT,WIS,CHA|2",
				"RACENAME:Dwarf%		BASEAGE:188	MAXAGE:249	AGEDIEROLL:10d6+1",
				"AGESET:3|Venerable	BONUS:STAT|STR,CON,DEX|-6	BONUS:STAT|INT,WIS,CHA|3",
				"RACENAME:Dwarf%		BASEAGE:250	MAXAGE:450	AGEDIEROLL:2d100"};

	/**
	 * The base race used in the bio set data.
	 */
	private final static String[] BASE_RACE_NAME =
			new String[]{"Human", "Dwarf", "Half-Elf"};

	private BioSetLoader loader;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
    @Override
	protected void setUp() throws Exception
	{
		super.setUp();

		loader = new BioSetLoader();
		BioSetLoaderTest.loadBioSet(Globals.getContext(), BIO_SET_DATA, loader);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
    @Override
	protected void tearDown() throws Exception
	{
		try
		{
			SettingsHandler.getGame().getBioSet().clearUserMap();
		}

		finally
		{
			super.tearDown();
		}
	}

	/**
	 * Basic constructor, name only.
	 */
	public BioSetLoaderTest()
	{
		// Do Nothing
	}

	/**
	 * Basic constructor, name only.
	 *
	 * @param name The name of the test class.
	 */
	public BioSetLoaderTest(final String name)
	{
		super(name);
	}

	/**
	 * Load the supplied bio set data.
	 *
	 * @param bioSetData The data to be loaded.
	 *
	 * @throws Exception If a problem occurs when loading the data
	 */
	public static void loadBioSet(LoadContext context, final String[] bioSetData, BioSetLoader loader) throws Exception
	{
		for (int i = 0; i < bioSetData.length; i++)
		{
			final String line = bioSetData[i];
			loader.parseLine(context, line, new URI("http://UNIT_TEST_CASE"));
		}
		SettingsHandler.getGame().setBioSet(loader.bioSet);
	}

	/**
	 * Run the tests standalone from the command line.
	 *
	 * @param args Command line args - ignored.
	 */
	public static void main(final String[] args)
	{
		junit.textui.TestRunner.run(BioSetLoaderTest.class);
	}

	/**
	 * Quick test suite creation - adds all methods beginning with "test".
	 *
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(BioSetLoaderTest.class);
	}

	/**
	 * Validate the data loaded in setUp() to verify that the parseLine fucntion in
	 * BioSetLoader is functioning properly.
	 */
	public void testParseLine()
	{
		final String[] TEST_TAGS =
				new String[]{"HAIR", "EYES", "SKINTONE", "AGEDIEROLL", "CLASS",
					"BASEAGE", "MAXAGE", "SEX"};
		final String[][] EXPECTED_VALUES =
				new String[][]{
					{
						"[Blond|Brown]",
						"[Blue]",
						"[Tanned|Pasty]",
						"[5d4, 3d6, 4d4+1, 4d10]",
						"[Barbarian,Rogue,Sorcerer[BASEAGEADD:1d4]|Bard,Fighter,Paladin,Ranger[BASEAGEADD:1d6]|Cleric,Druid,Monk,Wizard[BASEAGEADD:2d6]]",
						"[15, 35, 53, 70]",
						"[34, 52, 69, 110]",
						"[Male[BASEHT:58|HTDIEROLL:2d10|BASEWT:120|WTDIEROLL:2d4|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]Female[BASEHT:53|HTDIEROLL:2d10|BASEWT:85|WTDIEROLL:2d4|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]]"},
					{
						"[Blond|Brown|Bald|Gray]",
						"[Blue|Black|Brown|Gray]",
						"[Tanned|Pasty|Fair|Gray]",
						"[4d20+1d4, (7d8+1d6), 10d6+1, 2d100]",
						"[Barbarian,Rogue,Sorcerer[BASEAGEADD:3d6]|Bard,Fighter,Paladin,Ranger[BASEAGEADD:5d6]|Cleric,Druid,Monk,Wizard[BASEAGEADD:7d6]]",
						"[40, 125, 188, 250]",
						"[124, 187, 249, 450]",
						"[Male[BASEHT:45|HTDIEROLL:2d4|BASEWT:130|WTDIEROLL:2d6|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]Female[BASEHT:43|HTDIEROLL:2d4|BASEWT:100|WTDIEROLL:2d6|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]]"},
					{
						"[White|Pale Yellow|Silver-White|Black|Blue|Golden Blonde|Copper|Brown]",
						"[Violet|Silver|Pink|Pale Blue|Gold-flecked Green|Gold-flecked Blue|Green|Gold|Brown|Hazel]",
						"[Obsidian|Fair|Tanned|Pasty|Bronze|Dark Brown|Copper]",
						"[7d6, 5d6, 5d6+1, 6d10]",
						"[Barbarian,Rogue,Sorcerer[BASEAGEADD:1d6]|Bard,Fighter,Paladin,Ranger[BASEAGEADD:2d6]|Cleric,Druid,Monk,Wizard[BASEAGEADD:3d6]]",
						"[20, 62, 93, 125]",
						"[61, 92, 124, 185]",
						"[Male[BASEHT:55|HTDIEROLL:2d8|BASEWT:100|WTDIEROLL:2d4|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]Female[BASEHT:53|HTDIEROLL:2d8|BASEWT:80|WTDIEROLL:2d4|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]]"}};

		// Check the data loaded in setup to ensure that it has been loaded correctly.
		final BioSet currBioSet = SettingsHandler.getGame().getBioSet();
		List<String> baseRaceTag;
		for (int i = 0; i < BASE_RACE_NAME.length; i++)
		{
			final String raceName = BASE_RACE_NAME[i];

			for (int j = 0; j < TEST_TAGS.length; j++)
			{
				final String testArg = TEST_TAGS[j];
				baseRaceTag =
						currBioSet.getTagForRace(Constants.NONE, raceName,
							testArg);
				//				System.out.println(
				//					"Got '"
				//						+ testArg
				//						+ "' tag for race '"
				//						+ raceName
				//						+ "' of "
				//						+ baseRaceTag
				//						+ ".");
				assertEquals("BioSet tag " + testArg + " for race " + raceName
					+ ":", EXPECTED_VALUES[i][j], baseRaceTag.toString());
			}
		}

	}

	/**
	 * Check that a valid second bio set definition can be loaded.
	 * @throws Exception
	 */
	public void testParseSecondBioSetGood() throws Exception
	{
		assertEquals("No ogre bio details expected before load", "REGION:None\n\n", SettingsHandler
			.getGame().getBioSet().getRacePCCText("None", "Ogre"));
		String[] bioData2 = new String[]{
			"AGESET:0|Adulthood",
			"RACENAME:Ogre		CLASS:Barbarian,Rogue,Sorcerer[BASEAGEADD:1d4]|Bard,Fighter,Paladin,Ranger[BASEAGEADD:1d6]|Cleric,Druid,Monk,Wizard[BASEAGEADD:2d6]",
			"RACENAME:Ogre		SEX:Male[BASEHT:58|HTDIEROLL:2d10|BASEWT:120|WTDIEROLL:2d4|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]Female[BASEHT:53|HTDIEROLL:2d10|BASEWT:85|WTDIEROLL:2d4|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]",
			"RACENAME:Ogre		BASEAGE:15	MAXAGE:34	AGEDIEROLL:5d4	HAIR:Blond|Brown	EYES:Blue	SKINTONE:Tanned|Pasty",
			"AGESET:1|Middle Age",
			"RACENAME:Ogre		BASEAGE:35	MAXAGE:52	AGEDIEROLL:3d6",
			"AGESET:2|Old		BONUS:STAT|STR,CON,DEX|-3	BONUS:STAT|INT,WIS,CHA|2",
			"RACENAME:Ogre		BASEAGE:53	MAXAGE:69	AGEDIEROLL:4d4+1",
			"AGESET:3|Venerable	BONUS:STAT|STR,CON,DEX|-6	BONUS:STAT|INT,WIS,CHA|3",
			"RACENAME:Ogre		BASEAGE:70	MAXAGE:110	AGEDIEROLL:4d10",
			};
		BioSetLoaderTest.loadBioSet(Globals.getContext(), bioData2, loader);

		String racePCCText =
				SettingsHandler.getGame().getBioSet()
					.getRacePCCText("None", "Ogre");
		assertFalse("Ogre bio details expected after load but was "
			+ racePCCText, "REGION:None\n\n".equals(racePCCText));
		
	}

	/**
	 * Check that an invalid second bio set definition gets properly processed. 
	 * It is expected that the bioset will be loaded but the original name for 
	 * the age set will be used.
	 * @throws Exception
	 */
	public void testParseSecondBioSetBadName() throws Exception
	{
		assertEquals("No ogre bio details expected before load", "REGION:None\n\n", SettingsHandler
			.getGame().getBioSet().getRacePCCText("None", "Ogre"));
		String[] bioData2 = new String[]{
			"AGESET:0|Bad",
			"RACENAME:Ogre		CLASS:Barbarian,Rogue,Sorcerer[BASEAGEADD:1d4]|Bard,Fighter,Paladin,Ranger[BASEAGEADD:1d6]|Cleric,Druid,Monk,Wizard[BASEAGEADD:2d6]",
			"RACENAME:Ogre		SEX:Male[BASEHT:58|HTDIEROLL:2d10|BASEWT:120|WTDIEROLL:2d4|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]Female[BASEHT:53|HTDIEROLL:2d10|BASEWT:85|WTDIEROLL:2d4|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]",
			"RACENAME:Ogre		BASEAGE:15	MAXAGE:34	AGEDIEROLL:5d4	HAIR:Blond|Brown	EYES:Blue	SKINTONE:Tanned|Pasty",
			"AGESET:1|Middle Age",
			"RACENAME:Ogre		BASEAGE:35	MAXAGE:52	AGEDIEROLL:3d6",
			"AGESET:2|Old		BONUS:STAT|STR,CON,DEX|-3	BONUS:STAT|INT,WIS,CHA|2",
			"RACENAME:Ogre		BASEAGE:53	MAXAGE:69	AGEDIEROLL:4d4+1",
			"AGESET:3|Venerable	BONUS:STAT|STR,CON,DEX|-6	BONUS:STAT|INT,WIS,CHA|3",
			"RACENAME:Ogre		BASEAGE:70	MAXAGE:110	AGEDIEROLL:4d10",
			};
		BioSetLoaderTest.loadBioSet(Globals.getContext(), bioData2, loader);
		
		String racePCCText =
				SettingsHandler.getGame().getBioSet()
					.getRacePCCText("None", "Ogre");
		assertTrue(
			"Expected details to be against original ageset name but was "
				+ racePCCText,
			racePCCText
				.startsWith("REGION:None\n\nAGESET:0|Adulthood\nRACENAME:Ogre"));
	}
}

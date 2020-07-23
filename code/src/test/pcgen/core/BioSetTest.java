/*
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
 */
package pcgen.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;
import java.util.Optional;

import pcgen.AbstractCharacterTestCase;
import pcgen.LocaleDependentTestCase;
import pcgen.cdom.enumeration.PCStringKey;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.util.CControl;
import pcgen.core.display.CharacterDisplay;
import pcgen.output.channel.ChannelUtilities;
import pcgen.output.channel.compat.AgeCompat;
import pcgen.output.channel.compat.HairColorCompat;
import pcgen.persistence.lst.BioSetLoader;
import pcgen.persistence.lst.BioSetLoaderTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A collection of tests to validate the functioning of the core BioSet class.
 */
@SuppressWarnings("nls")
public class BioSetTest extends AbstractCharacterTestCase
{
	private static final String[] BIO_SET_DATA =
			{
				"AGESET:0|Adulthood",
				"RACENAME:Human%		CLASS:Barbarian,Rogue,Sorcerer[BASEAGEADD:1d4]|"
				+ "Bard,Fighter,Paladin,Ranger[BASEAGEADD:1d6]|Cleric,Druid,Monk,Wizard[BASEAGEADD:2d6]",
				"RACENAME:Human%		SEX:Male[BASEHT:58|HTDIEROLL:2d10|BASEWT:120|WTDIEROLL:2d4|"
				+ "TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]Female[BASEHT:53|HTDIEROLL:2d10|BASEWT:85|WTDIEROLL:2d4|"
						+ "TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]",
				"RACENAME:Human%		BASEAGE:15	MAXAGE:34	AGEDIEROLL:5d4	HAIR:Blond|Brown	EYES:Blue	"
						+ "SKINTONE:Tanned|Pasty",
				"AGESET:1|Middle Age	BONUS:STAT|STR,CON,DEX|-1	BONUS:STAT|INT,WIS,CHA|1",
				"RACENAME:Human%		BASEAGE:35	MAXAGE:52	AGEDIEROLL:3d6",
				"AGESET:2|Old		BONUS:STAT|STR,CON,DEX|-3	BONUS:STAT|INT,WIS,CHA|2",
				"RACENAME:Human%		BASEAGE:53	MAXAGE:69	AGEDIEROLL:4d4+1",
				"AGESET:3|Venerable	BONUS:STAT|STR,CON,DEX|-6	BONUS:STAT|INT,WIS,CHA|3",
				"RACENAME:Human%		BASEAGE:70	MAXAGE:110	AGEDIEROLL:4d10"};

	@BeforeEach
    @Override
	public void setUp() throws Exception
	{
		super.setUp();
		BioSetLoaderTest.loadBioSet(Globals.getContext(), BIO_SET_DATA,
			new BioSetLoader());
		finishLoad();
	}

	@AfterEach
	@Override
	protected void tearDown() throws Exception
	{
		SettingsHandler.getGameAsProperty().get().getBioSet().clearUserMap();

		super.tearDown();
	}

	/**
	 * Verify that the randomize function in BioSet
	 * is functioning properly.
	 */
	@Test
	public void testRandomize()
	{
		final int[] BASE_AGE = {15, 35, 53, 70};
		final int[] MAX_AGE = {34, 52, 69, 110};

		final BioSet currBioSet = SettingsHandler.getGameAsProperty().get().getBioSet();
		final PlayerCharacter pc = getCharacter();
		final Race human = new Race();
		human.setName("NAME_Human");
		human.put(StringKey.KEY_NAME, "Human");
		pc.setRace(human);
		for (int ageCat = 0; ageCat < MAX_AGE.length; ageCat++)
		{
			currBioSet.randomize("AGECAT" + ageCat, pc);
			final int age = AgeCompat.getCurrentAge(pc.getCharID());
			//System.out.println("Age for cat " + ageCat + " is " + age + ".");
			assertTrue(
					(age >= BASE_AGE[ageCat] && age <= MAX_AGE[ageCat]), "Generated age " + age + " is not between "
						+ BASE_AGE[ageCat] + " and " + MAX_AGE[ageCat]);
		}
		LocaleDependentTestCase.before(Locale.US);
		currBioSet.randomize("AGE.HT.WT.EYES.HAIR.SKIN", pc);
		LocaleDependentTestCase.after();
		assertTrue((pc.getDisplay().getHeight() >= 58 && pc
					.getDisplay().getHeight() <= 78), "Generated height " + pc.getDisplay().getHeight()
						+ " is not in required range.");
		assertTrue((pc.getDisplay().getWeight() >= 120 && pc
					.getDisplay().getWeight() <= 280), "Generated weight " + pc.getDisplay().getWeight()
						+ " is not in required range.");
		assertEquals("Blue", pc.getSafeStringFor(PCStringKey.EYECOLOR), "Generated eye colour " + pc.getSafeStringFor(PCStringKey.EYECOLOR)
				+ " is not valid.");
		assertTrue(
			("Blond".equals(HairColorCompat.getCurrentHairColor(pc.getCharID()))
				|| "Brown".equals(
					HairColorCompat.getCurrentHairColor(pc.getCharID()))),
			"Generated hair colour "
				+ HairColorCompat.getCurrentHairColor(pc.getCharID())
				+ " is not valid.");
		String skinColor = (String) ChannelUtilities
			.readControlledChannel(pc.getCharID(), CControl.SKINCOLORINPUT);
		assertTrue(("Pasty".equals(skinColor) || "Tanned".equals(skinColor)),
			"Generated skin colour " + skinColor + " is not valid.");
	}

	/**
	 * Test the age set
	 */
	@Test
	public void testAgeSet()
	{
		final PlayerCharacter pc = getCharacter();
		CharacterDisplay display = pc.getDisplay();
		final Race human = new Race();
		human.setName("Human");
		pc.setRace(human);
		AgeCompat.setCurrentAge(pc.getCharID(), 12);
		int idx = display.getAgeSetIndex();
		assertEquals(0, idx, "Ageset for " + AgeCompat.getCurrentAge(pc.getCharID()) + ".");

		AgeCompat.setCurrentAge(pc.getCharID(), 17);
		idx = display.getAgeSetIndex();
		assertEquals(0, idx, "Ageset for " + AgeCompat.getCurrentAge(pc.getCharID()) + ".");

		AgeCompat.setCurrentAge(pc.getCharID(), 36);
		idx = display.getAgeSetIndex();
		assertEquals(1, idx, "Ageset for " + AgeCompat.getCurrentAge(pc.getCharID()) + ".");

		AgeCompat.setCurrentAge(pc.getCharID(), 54);
		idx = display.getAgeSetIndex();
		assertEquals(2, idx, "Ageset for " + AgeCompat.getCurrentAge(pc.getCharID()) + ".");

		AgeCompat.setCurrentAge(pc.getCharID(), 72);
		idx = display.getAgeSetIndex();
		assertEquals(3, idx, "Ageset for " + AgeCompat.getCurrentAge(pc.getCharID()) + ".");

		Optional<Region> region = pc.getDisplay().getRegion();
		SettingsHandler.getGameAsProperty().get().getBioSet().getAgeSet(region, idx);

	}

	@Override
	protected void defaultSetupEnd()
	{
		//Nothing, we will trigger ourselves
	}
}

/*
 * LevelAbility.java
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
 * $$Revision$$
 * $$Date$$
 * $$Time$$
 * 
 * $$id$$
 */
package pcgen.core.levelability;

import java.awt.HeadlessException;
import java.util.ArrayList;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.gui.utils.SwingChooser;
import pcgen.util.Logging;
import pcgen.util.TestHelper;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

/**
 * Tests for Level Ability Class Skills
 */
public class LevelAbilityClassSkillsTest extends AbstractCharacterTestCase {

	PCClass pcClass;
	Race    emptyRace = new Race();
    boolean firstTime = true;

    /*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		if (firstTime) {
			firstTime = false;

			pcClass = new PCClass();

			TestHelper.makeSkill("Bluff",              "Charisma",               "CHA", "YES", "NO");
			TestHelper.makeSkill("Listen",             "Wisdom",                 "WIS", "YES", "NO");
			TestHelper.makeSkill("Move Silently",      "Dexterity",              "DEX", "YES", "YES");
			TestHelper.makeSkill("Knowledge (Arcana)", "Intelligence.Knowledge", "INT", "NO",  "NO");
			TestHelper.makeSkill("Knowledge (Dungeoneering)", "Intelligence.Knowledge", "INT", "NO", "NO");
		}

		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, pcClass);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		pcClass = null;
		super.tearDown();
	}

	/**
	 * Test method for 'pcgen.core.levelability.LevelAbilityClassSkills.getChoicesList(String, PlayerCharacter)'
	 */
	public void testGetChoicesList1() {
		ChooserFactory.setInterfaceClassname(SwingChooser.class.getName());

		final LevelAbility ability = LevelAbility.createAbility(pcClass, 1, "CLASSSKILLS(Bluff,Listen,Move Silently)2");
		is (new Integer(ability.level()),      eq(1),    "Is level correct");
		is (new Boolean(ability.canProcess()), eq(true), "Can we process this LevelAbility");


		try
		{
			final ChooserInterface c = ChooserFactory.getChooserInstance();
			ability.setType(getCharacter());
			final String bString = ability.prepareChooser(c, getCharacter());
			is (new Integer(c.getPool()), eq(2), "Ar two choices being offered");

			final ArrayList choicesList = (ArrayList) ability.getChoicesList(bString, getCharacter());
			is (new Integer(choicesList.size()), eq(3), "Ar there three choices available");

			String s = (String) choicesList.get(0);
			is(s, strEq("Bluff"), "Is First choice correct");
			s = (String) choicesList.get(1);
			is(s, strEq("Listen"), "Is Second Choice");
			s = (String) choicesList.get(2);
			is(s, strEq("Move Silently"), "Is Third Choice");
		}
		catch(HeadlessException e)
		{
			Logging.debugPrint("Ignoring Headless excpetion.");
		}
	}

	/**
	 * Test method for 'pcgen.core.levelability.LevelAbilityClassSkills.getChoicesList(String, PlayerCharacter)'
	 */
	public void testGetChoicesList2() {
		ChooserFactory.setInterfaceClassname(SwingChooser.class.getName());

		final LevelAbility ability = LevelAbility.createAbility(pcClass, 1, "CLASSSKILLS(Bluff,Listen,Knowledge (Arcana))2");
		is (new Integer(ability.level()),      eq(1),    "Is level correct");
		is (new Boolean(ability.canProcess()), eq(true), "Can we process this LevelAbility");

		Skill mySkill = new Skill();
		mySkill.setName("");
		Globals.getSkillList().add(mySkill);


		
		try
		{
			final ChooserInterface c = ChooserFactory.getChooserInstance();
			ability.setType(getCharacter());
			final String bString = ability.prepareChooser(c, getCharacter());
			is (new Integer(c.getPool()), eq(2), "Ar two choices being offered");

			final ArrayList choicesList = (ArrayList) ability.getChoicesList(bString, getCharacter());
			is (new Integer(choicesList.size()), eq(3), "Ar there three choices available");

			String s = (String) choicesList.get(0);
			is(s, strEq("Bluff"), "Is First choice correct");
			s = (String) choicesList.get(1);
			is(s, strEq("Listen"), "Is Second Choice");
			s = (String) choicesList.get(2);
			is(s, strEq("Knowledge (Arcana)"), "Is Third Choice");
		}
		catch(HeadlessException e)
		{
			Logging.debugPrint("Ignoring Headless excpetion.");
		}
	}
	
	/**
	 * Test method for 'pcgen.core.levelability.LevelAbilityClassSkills.getChoicesList(String, PlayerCharacter)'
	 */
	public void testGetChoicesList3() {
		ChooserFactory.setInterfaceClassname(SwingChooser.class.getName());

		final LevelAbility ability = LevelAbility.createAbility(pcClass, 1, "CLASSSKILLS(Bluff,Listen,Knowledge (Arcana),Knowledge (Dungeoneering))2");
		is (new Integer(ability.level()),      eq(1),    "Is level correct");
		is (new Boolean(ability.canProcess()), eq(true), "Can we process this LevelAbility");

		Skill mySkill = new Skill();
		mySkill.setName("");
		Globals.getSkillList().add(mySkill);


		
		try
		{
			final ChooserInterface c = ChooserFactory.getChooserInstance();
			ability.setType(getCharacter());
			final String bString = ability.prepareChooser(c, getCharacter());
			is (new Integer(c.getPool()), eq(2), "Ar two choices being offered");

			final ArrayList choicesList = (ArrayList) ability.getChoicesList(bString, getCharacter());
			is (new Integer(choicesList.size()), eq(4), "Are there four choices available");

			String s = (String) choicesList.get(0);
			is(s, strEq("Bluff"), "Is First choice correct");
			s = (String) choicesList.get(1);
			is(s, strEq("Listen"), "Is Second Choice");
			s = (String) choicesList.get(2);
			is(s, strEq("Knowledge (Arcana)"), "Is Third Choice");
			s = (String) choicesList.get(3);
			is(s, strEq("Knowledge (Dungeoneering)"), "Is Fourth Choice");
		}
		catch(HeadlessException e)
		{
			Logging.debugPrint("Ignoring Headless excpetion.");
		}
	}
}

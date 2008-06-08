/*
 * ClassSkillsChoiceManagerTest.java
 * Copyright 2005 (C) Andrew Wilson <nuance@sourceforge.net>
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
 * Created on Oct 7, 2005
 *
 * $Author: nuance $
 * $Date: 2006-03-26 08:00:03 +0100 (Sun, 26 Mar 2006) $
 * $Revision: 471 $
 *
 */
package pcgen.core.chooser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.SkillArmorCheck;
import pcgen.core.EquipmentList;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.util.TestHelper;

/**
 * {@code ClassSkillsChoiceManagerTest} test that the ClassSkillsChoiceManager class is functioning correctly.
 *
 * @author Andrew Wilson <nuance@sourceforge.net>
 */

public class ClassSkillsChoiceManagerTest extends AbstractCharacterTestCase
{

	PCClass pcClass;
	Race emptyRace = new Race();
	boolean firstTime = true;
	PlayerCharacter myChar;

	/**
	 * Constructs a new {@code ClassSkillsChoiceManagerTest}.
	 */
	public ClassSkillsChoiceManagerTest()
	{
		// Do Nothing
	}

	protected void setUp() throws Exception
	{
		super.setUp();

		if (firstTime)
		{
			firstTime = false;

			TestHelper.makeSkill("Bluff", "Charisma", "CHA", true, SkillArmorCheck.NONE);
			TestHelper.makeSkill("Listen", "Wisdom", "WIS", true, SkillArmorCheck.NONE);
			TestHelper.makeSkill("Move Silently", "Dexterity", "DEX", true,
					SkillArmorCheck.YES);
			TestHelper.makeSkill("Knowledge (Arcana)",
				"Intelligence.Knowledge", "INT", false, SkillArmorCheck.NONE);
			TestHelper.makeSkill("Knowledge (Dungeoneering)",
				"Intelligence.Knowledge", "INT", false, SkillArmorCheck.NONE);

			pcClass = new PCClass();
			pcClass.addCSkill("KEY_Bluff");
			pcClass.addCSkill("KEY_Listen");
			pcClass.addCSkill("TYPE.Knowledge");
			pcClass.addCcSkill("KEY_Move Silently");

		}

		myChar = getCharacter();
		myChar.incrementClassLevel(1, pcClass);
	}

	/**
	 * Test the constructor
	 */
	public void test001()
	{
		PObject pObj = new PObject();
		pObj.setName("My PObject");
		pObj.setChoiceString("NUMCHOICES=2|CSKILLS");
		is(pObj.getChoiceString(), strEq("NUMCHOICES=2|CSKILLS"));

		PlayerCharacter aPC = getCharacter();

		ChoiceManagerList choiceManager =
				ChooserUtilities.getChoiceManager(pObj, null, aPC);
		is(choiceManager, not(eq(null)), "Found the chooser");

		is(choiceManager.typeHandled(), strEq("CSKILLS"),
			"got expected chooser");

		try
		{
			Class<? extends ChoiceManagerList> cMClass =
					choiceManager.getClass();

			Field aField =
					(Field) TestHelper.findField(cMClass, "numberOfChoices");
			is(aField.get(choiceManager), eq(2));

			aField = (Field) TestHelper.findField(cMClass, "choices");
			List choices = (List) aField.get(choiceManager);
			is(choices.size(), eq(0));
		}
		catch (IllegalAccessException e)
		{
			System.out.println(e);
		}
	}

	/**
	 * Test the choice constructing routine
	 */
	public void test002()
	{
		PObject pObj = new PObject();
		pObj.setName("My PObject");
		pObj.setChoiceString("NUMCHOICES=2|CSKILLS");
		is(pObj.getChoiceString(), strEq("NUMCHOICES=2|CSKILLS"));

		PlayerCharacter aPC = getCharacter();

		ChoiceManagerList choiceManager =
				ChooserUtilities.getChoiceManager(pObj, null, aPC);
		is(choiceManager, not(eq(null)), "Found the chooser");

		is(choiceManager.typeHandled(), strEq("CSKILLS"),
			"got expected chooser");

		try
		{
			Class<? extends ChoiceManagerList> cMClass =
					choiceManager.getClass();

			Field aField =
					(Field) TestHelper.findField(cMClass, "numberOfChoices");
			is(aField.get(choiceManager), eq(2));

			aField = (Field) TestHelper.findField(cMClass, "choices");
			List choices = (List) aField.get(choiceManager);
			is(choices.size(), eq(0));

			ArrayList avail = new ArrayList();
			ArrayList selected = new ArrayList();

			choiceManager.getChoices(myChar, avail, selected);

			is(avail.size(), eq(4), "Available choices is correct size");
			is(selected.size(), eq(0), "Selected choices is correct size");
		}
		catch (IllegalAccessException e)
		{
			System.out.println(e);
		}
	}

	/**
	 * Test the choice constructing routine
	 */
	public void test003()
	{
		PObject pObj = new PObject();
		pObj.setName("My PObject");
		pObj.setChoiceString("NUMCHOICES=2|CSKILLS");
		is(pObj.getChoiceString(), strEq("NUMCHOICES=2|CSKILLS"));

		PlayerCharacter aPC = getCharacter();

		ChoiceManagerList choiceManager =
				ChooserUtilities.getChoiceManager(pObj, null, aPC);
		is(choiceManager, not(eq(null)), "Found the chooser");

		is(choiceManager.typeHandled(), strEq("CSKILLS"),
			"got expected chooser");

		try
		{
			Class<? extends ChoiceManagerList> cMClass =
					choiceManager.getClass();

			Field aField =
					(Field) TestHelper.findField(cMClass, "numberOfChoices");
			is(aField.get(choiceManager), eq(2));

			aField = (Field) TestHelper.findField(cMClass, "choices");
			List choices = (List) aField.get(choiceManager);
			is(choices.size(), eq(0));

			ArrayList avail = new ArrayList();
			ArrayList selected = new ArrayList();

			choiceManager.getChoices(myChar, avail, selected);

			is(avail.size(), eq(4), "Available choices is correct size");
			is(selected.size(), eq(0), "Selected choices is correct size");
			List<String> inAvail = new ArrayList<String>(4);
			for (Object o : avail)
			{
				inAvail.add(o.toString());
			}
			assertTrue(inAvail.contains("Bluff"));
			assertTrue(inAvail.contains("Listen"));
			assertTrue(inAvail.contains("Knowledge (Arcana)"));
			assertTrue(inAvail.contains("Knowledge (Dungeoneering)"));
		}
		catch (IllegalAccessException e)
		{
			System.out.println(e);
		}
	}
}

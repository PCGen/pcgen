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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SkillArmorCheck;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.util.TestHelper;

/**
 * Tests for Level Ability Class Skills
 */
@SuppressWarnings("nls")
public class AddClassSkillsTest extends AbstractCharacterTestCase
{

	PCClass pcClass;
	Race emptyRace = new Race();
	boolean firstTime = true;

	/**
	 * @see pcgen.AbstractCharacterTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		if (firstTime)
		{
			firstTime = false;

			pcClass = new PCClass();

			TestHelper.makeSkill("Bluff", "Charisma", "CHA", true, SkillArmorCheck.NONE);
			TestHelper.makeSkill("Listen", "Wisdom", "WIS", true, SkillArmorCheck.NONE);
			TestHelper.makeSkill("Move Silently", "Dexterity", "DEX", true,
					SkillArmorCheck.YES);
			TestHelper.makeSkill("Knowledge (Arcana)",
				"Intelligence.Knowledge", "INT", false, SkillArmorCheck.NONE);
			TestHelper.makeSkill("Knowledge (Dungeoneering)",
				"Intelligence.Knowledge", "INT", false, SkillArmorCheck.NONE);
		}

		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, pcClass);
	}

	/**
	 * @see pcgen.AbstractCharacterTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		pcClass = null;
		super.tearDown();
	}

	/**
	 * Test method for 'pcgen.core.levelability.LevelAbilityClassSkills.getChoicesList(String, PlayerCharacter)'
	 */
	public void testBasicChoicesList()
	{
		PCClass po = new PCClass();
		
		Globals.getContext().unconditionallyProcess(po, "ADD",
				"CLASSSKILLS|2|KEY_Bluff,KEY_Listen,KEY_Move Silently");
		Globals.getContext().resolveReferences();
		List<TransitionChoice<?>> choiceList = po.getListFor(ListKey.ADD);
		assertEquals(1, choiceList.size());
		TransitionChoice<?> choice = choiceList.get(0);
		Set<?> choiceSet = choice.getChoices().getSet(getCharacter());
		assertEquals(3, choiceSet.size());
		assertEquals(2, choice.getCount().resolve(getCharacter(), ""));
		
		ArrayList<String> choiceStrings = new ArrayList<String>();
		for (Object o : choiceSet)
		{
			choiceStrings.add(o.toString());
		}
		assertTrue(choiceStrings.contains("Bluff"));
		assertTrue(choiceStrings.contains("Listen"));
		assertTrue(choiceStrings.contains("Move Silently"));
	}

	/**
	 * Test method for 'pcgen.core.levelability.LevelAbilityClassSkills.getChoicesList(String, PlayerCharacter)'
	 */
	public void testGetChoicesListWithParens()
	{
		PCClass po = new PCClass();

		Globals.getContext().unconditionallyProcess(po, "ADD",
				"CLASSSKILLS|2|KEY_Bluff,KEY_Listen,KEY_Knowledge (Arcana)");
		Globals.getContext().resolveReferences();

		List<TransitionChoice<?>> choiceList = po.getListFor(ListKey.ADD);
		assertEquals(1, choiceList.size());
		TransitionChoice<?> choice = choiceList.get(0);
		Set<?> choiceSet = choice.getChoices().getSet(getCharacter());
		assertEquals(3, choiceSet.size());
		assertEquals(2, choice.getCount().resolve(getCharacter(), ""));
		
		ArrayList<String> choiceStrings = new ArrayList<String>();
		for (Object o : choiceSet)
		{
			choiceStrings.add(o.toString());
		}
		assertTrue(choiceStrings.contains("Bluff"));
		assertTrue(choiceStrings.contains("Listen"));
		assertTrue(choiceStrings.contains("Knowledge (Arcana)"));
	}

	/**
	 * Test method for 'pcgen.core.levelability.LevelAbilityClassSkills.getChoicesList(String, PlayerCharacter)'
	 */
	public void testGetChoicesListWithClassSkill()
	{
		PCClass po = new PCClass();

		PCTemplate pct = new PCTemplate();
		Skill bluff = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(Skill.class, "KEY_Bluff");
		pct.addToListFor(ListKey.CSKILL, CDOMDirectSingleRef.getRef(bluff));
		getCharacter().addTemplate(pct);

		Globals.getContext().unconditionallyProcess(po, "ADD",
				"CLASSSKILLS|2|KEY_Bluff,KEY_Listen,KEY_Knowledge (Arcana)");
		Globals.getContext().resolveReferences();

		List<TransitionChoice<?>> choiceList = po.getListFor(ListKey.ADD);
		assertEquals(1, choiceList.size());
		TransitionChoice<?> choice = choiceList.get(0);
		Set<?> choiceSet = choice.getChoices().getSet(getCharacter());
		assertEquals(2, choiceSet.size());
		assertEquals(2, choice.getCount().resolve(getCharacter(), ""));
		
		ArrayList<String> choiceStrings = new ArrayList<String>();
		for (Object o : choiceSet)
		{
			choiceStrings.add(o.toString());
		}
		assertTrue(choiceStrings.contains("Listen"));
		assertTrue(choiceStrings.contains("Knowledge (Arcana)"));
	}

}

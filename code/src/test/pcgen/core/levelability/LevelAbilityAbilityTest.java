/*
 * LevelAbilityAbilityTest.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on February 6, 2007
 *
 * Current Ver: $Revision: 1777 $
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006-12-17 15:36:01 +1100 (Sun, 17 Dec 2006) $
 *
 */
package pcgen.core.levelability;

import java.awt.HeadlessException;
import java.util.List;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.gui.utils.SwingChooser;
import pcgen.util.Logging;
import pcgen.util.TestHelper;
import pcgen.util.chooser.ChooserFactory;
import plugin.lsttokens.AddLst;

/**
 * <code>LevelAbilityAbilityTest</code> checks the fucntion of the 
 * LevelAbilityAbility class.
 *
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: $
 */
public class LevelAbilityAbilityTest extends AbstractCharacterTestCase
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

			TestHelper.makeAbility("FtrFeat1", AbilityCategory.FEAT.getAbilityCategory(), "FIGHTER");
			TestHelper.makeAbility("NonFtrFeat", AbilityCategory.FEAT.getAbilityCategory(), "GENERAL");
			TestHelper.makeAbility("FtrAbility", "SpecialAbility", "FIGHTER");
			Ability ftrFeat2 = TestHelper.makeAbility("FtrFeat2", AbilityCategory.FEAT.getAbilityCategory(), "FIGHTER");
			ftrFeat2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
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
	public void testGetChoicesList1()
	{
		ChooserFactory.setInterfaceClassname(SwingChooser.class.getName());

		final LevelAbility ability =
				LevelAbility.createAbility(pcClass, 1,
					"ABILITY(CATEGORY=FEAT,TYPE.Fighter)");
		is(ability.level(), eq(1), "Is level correct");
		is(ability.canProcess(), eq(true), "Can we process this LevelAbility");

		try
		{
			//final ChooserInterface c = ChooserFactory.getChooserInstance();
			//is(c.getPool(), eq(1), "Is one choice being offered");
			final String bString = ability.getTagData();

			final List<String> choicesList =
					ability.getChoicesList(bString, getCharacter());
			is(choicesList.size(), eq(1), "Is there one choice available");

			is(choicesList.get(0), strEq("KEY_FtrFeat1"), "Is First choice correct");
		}
		catch (HeadlessException e)
		{
			Logging.debugPrint("Ignoring Headless excpetion.");
		}
	}
	
	public void testParse()
	{
		AddLst addParser = new AddLst();
		
		assertTrue("Parse of add ability should pass.", addParser.parse(emptyRace,
			"ABILITY|2|FEAT|NORMAL|Toughness", -9));
		LevelAbility ability = emptyRace.getLevelAbilityList().get(0);
		assertEquals("Level of parsed ability not correct", ability.level(), -9);
		//assertEquals("Level of parsed ability not correct", ability., -9);
		
		assertTrue("Parse of add ability should pass.", addParser.parse(emptyRace,
			"ABILITY|FEAT|NORMAL|Toughness", -9));
		
		assertFalse("Parse with no nature should fail.", addParser.parse(emptyRace,
			"ABILITY|2|FEAT|Toughness", -9));
	}
}

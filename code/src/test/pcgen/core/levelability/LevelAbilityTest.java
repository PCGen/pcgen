/*
 * LevelAbility.java
 * Copyright 2001 (C) Dmitry Jemerov <yole@spb.cityline.ru>
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
 * Created on July 23, 2001, 8:40 PM
 */
package pcgen.core.levelability;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.*;
import pcgen.gui.utils.SwingChooser;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

import java.awt.HeadlessException;
import java.util.ArrayList;

/**
 * JUnit 3.6 testcases for <code>pcgen.core.LevelAbility</code>.
 *
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 */
public class LevelAbilityTest extends AbstractCharacterTestCase
{
	PCClass pcClass;
	Race emptyRace = new Race();

	/**
	 * Constructor
	 * @param name
	 */
	public LevelAbilityTest(final String name)
	{
		super(name);
	}

	public void setUp() throws Exception
	{
		super.setUp();
		pcClass = new PCClass();
		final PlayerCharacter character = getCharacter();
		character.incrementClassLevel(1, pcClass);
	}

	public void tearDown() throws Exception
	{
		pcClass = null;
		super.tearDown();
	}

	/**
	 * Test the Lanaguage Level Ability
	 */
	public void testLanguage()
	{
		ChooserFactory.setInterfaceClassname(SwingChooser.class.getName());

		final LevelAbility ability = LevelAbility.createAbility(pcClass, 1, "Language(Elven,Dwarvish)");
		assertTrue(ability.level() == 1);
		assertTrue(ability.canProcess());

		Language lang = new Language();
		lang.setName("Dwarvish");
		Globals.getLanguageList().add(lang);

		lang = new Language();
		lang.setName("Elven");
		Globals.getLanguageList().add(lang);

		try
		{
			final ChooserInterface c = ChooserFactory.getChooserInstance();
			ability.setType(getCharacter());
			final String bString = ability.prepareChooser(c, getCharacter());
			assertTrue(c.getPool() == 1);

			final ArrayList choicesList = (ArrayList) ability.getChoicesList(bString, getCharacter());
			assertEquals(choicesList.size(), 2);

			String s = (String) choicesList.get(0);
			is(s, strEq("Dwarvish"));
			s = (String) choicesList.get(1);
			is(s, strEq("Elven"));
		}
		catch(HeadlessException e)
		{
			Logging.debugPrint("Ignoring Headless excpetion.");
		}
	}
}

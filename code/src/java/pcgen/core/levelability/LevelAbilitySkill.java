/*
 * LevelAbilitySkill.java
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on Sep 26, 2004
 *
 * $Id$
 *
 */
package pcgen.core.levelability;

import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.gui.CharacterInfo;
import pcgen.gui.PCGen_Frame1;
import pcgen.util.chooser.ChooserInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <code>LevelAbilitySkill</code> represents skills the character
 * is granted when going up a level.
 * (an ADD:SKILL line in the LST file).
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
class LevelAbilitySkill extends LevelAbility
{

	LevelAbilitySkill(final PObject aOwner, final int aLevel, final String aList)
	{
		super(aOwner, aLevel, aList);
	}

	/**
	 * Set the type property of this LevelAbility
	 *
	 * @param  aPC
	 */
	protected void setType(final PlayerCharacter aPC)
	{
		type = SKILL;
	}

	/**
	 * Generates the list of tokens to be shown in the chooser from the list of
	 * skills of given type.
	 * @param bString
	 * @param aPC
	 * @return choices list
	 */
	List getChoicesList(final String bString, final PlayerCharacter aPC)
	{
		final List aArrayList = new ArrayList();

		final StringTokenizer aTok = new StringTokenizer(rawTagData.substring(rawTagData
			.lastIndexOf('(') + 1, rawTagData.lastIndexOf(')')), ",", false);

		while (aTok.hasMoreTokens())
		{
			final String toAdd = aTok.nextToken();
			final Skill aSkill = Globals.getSkillKeyed(toAdd);

			if ((aSkill != null))
			{
				aArrayList.add(aSkill.getKeyName());
			}
		}

		return aArrayList;
	}

	/**
	 * Performs the initial setup of a chooser.
	 * @param chooser
	 * @param aPC
	 * @return String
	 */
	String prepareChooser(final ChooserInterface chooser, PlayerCharacter aPC)
	{
		setNumberofChoices(chooser, aPC);
		chooser.setTitle("Skill Choice");

		return rawTagData.substring(6);
	}


	/**
	 * Processes a single token in the comma-separated list of the ADD: field
	 * and adds the choices to be shown to the ArrayList.
	 *
	 * @param  aToken        the token to be processed.
	 * @param  anArrayList   the list to add the choice to.
	 * @param  aPC           unused, only here to match signature of method in
	 *                       superclass.
	 */
	void processToken(
		final String          aToken,
		final List            anArrayList,
		final PlayerCharacter aPC)
	{
		anArrayList.add(aToken);
	}

	/**
	 * Process the choice selected by the user.
	 * @param selectedList
	 * @param aPC
	 * @param pcLevelInfo
	 * @param aArrayList
	 */
	public boolean processChoice(final List aArrayList, final List selectedList, final PlayerCharacter aPC, final PCLevelInfo pcLevelInfo)
	{
		boolean changed = false;

		for (int index = 0; index < selectedList.size(); ++index)
		{
			final String sString = selectedList.get(index).toString();
			Skill skillToAdd = Globals.getSkillKeyed(sString);
			if (skillToAdd != null)
			{
				skillToAdd = aPC.addSkill(skillToAdd);
				skillToAdd.modRanks(1.0, null, true, aPC);
				changed = true;
			}
		}

		if (changed && Globals.getUseGUI())
		{
			final CharacterInfo pane = PCGen_Frame1.getCharacterPane();
			if (pane != null)
			{
				pane.setPaneForUpdate(pane.infoSkills());
				pane.refresh();
			}
		}
		return true;
	}
}

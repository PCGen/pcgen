/**
 * SkillListChoiceManager.java
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
 * Current Version: $Revision: 285 $
 * Last Editor:     $Author: nuance $
 * Last Edited:     $Date: 2006-03-17 15:19:49 +0000 (Fri, 17 Mar 2006) $
 *
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.util.enumeration.Visibility;

/**
 * This is one of the choosers that deals with choosing a skill.
 */
public class SkillListChoiceManager extends AbstractBasicStringChoiceManager
{

	/**
	 * Make a new Skill List chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public SkillListChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		setTitle("Skill Choice");
		List<String> list = getChoiceList();
		if (list == null || list.size() > 1)
		{
			throw new IllegalArgumentException(
					"Choice List for SkillListChoiceManager must be 1 item");
		}
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	@Override
	public void getChoices(
			final PlayerCharacter aPc,
			final List<String>            availableList,
			final List<String>            selectedList)
	{
		String choiceVal = getChoiceList().get(0);
		if ((choiceVal.length() > 0) && !"LIST".equals(choiceVal))
		{
			StringTokenizer choiceTok = new StringTokenizer(choiceVal, ",");

			while (choiceTok.hasMoreTokens())
			{
				availableList.add(choiceTok.nextToken());
			}
		}

		else // if it was LIST
		{
			for ( Skill skill : Globals.getPartialSkillList(Visibility.DISPLAY_ONLY) )
			{
				availableList.add(skill.getKeyName());
			}
		}

		pobject.addAssociatedTo(selectedList);
		setPreChooserChoices(selectedList.size());
	}

	/**
	 * Associate a choice with the pobject.
	 * @param aPc
	 * @param item the choice to associate
	 * @param prefix
	 */
	@Override
	protected void associateChoice(
			PlayerCharacter pc, final String          item)
	{
		super.associateChoice(pc, item);

		if (pobject != null && pobject instanceof Ability)
		{
			Ability ability = (Ability) pobject;
			addSkillToAbility( ability, item );
		}
	}

	protected void addSkillToAbility( final Ability anAbility, final String aSkillKey )
	{
		anAbility.addCSkill( aSkillKey );
	}
}

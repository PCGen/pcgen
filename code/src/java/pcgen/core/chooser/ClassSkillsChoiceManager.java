/**
 * ClassSkillsChoiceManager.java
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

import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;

import java.util.List;
import java.util.ArrayList;

/**
 * This is the chooser that deals with choosing from among a PCs class skills.
 */
public class ClassSkillsChoiceManager extends AbstractBasicPObjectChoiceManager<Skill> {

	/**
	 * Make a new Class Skills chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public ClassSkillsChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		setTitle("Skill Choice");
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
			final List<Skill>            availableList,
			final List<Skill>            selectedList)
	{
		for ( Skill skill : Globals.getSkillList() )
		{
			SkillCost sCost = skill.costForPCClassList(aPc.getClassList(), aPc);

			if (sCost.equals(SkillCost.CLASS))
			{
				availableList.add(skill);
			}
		}

		List<String> associatedKeys = new ArrayList<String>();
		pobject.addAssociatedTo(associatedKeys);
		for ( String key : associatedKeys )
		{
			Skill skill = Globals.getSkillKeyed(key);
			if ( skill != null )
			{
				selectedList.add( skill );
			}
		}
		setPreChooserChoices(selectedList.size());
	}

}

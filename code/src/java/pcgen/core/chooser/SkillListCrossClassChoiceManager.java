/**
 * SkillListCrossClassChoiceManager.java
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

import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;

import java.util.Iterator;

/**
 * This is one of the choosers that deals with choosing a skill.
 */
public class SkillListCrossClassChoiceManager extends SkillListChoiceManager {

	/**
	 * Make a new Skill List chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public SkillListCrossClassChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		title = "Skill Choice";
		chooserHandled = "CCSKILLIST";
		
		if (choices != null && choices.size() > 0 &&
				((String) choices.get(0)).equals(chooserHandled)) {
			choices = choices.subList(1, choices.size());
		}
	}

	/**
	 * Associate a choice with the pobject.
	 * 
	 * @param aPc 
	 * @param item the choice to associate
	 * @param prefix 
	 */
	protected void associateChoice(
			final PlayerCharacter aPc,
			final String          item,
			final String          prefix)
	{
		super.associateChoice(aPc, item, prefix);

		if (pobject != null && pobject instanceof Ability)
		{
			Ability ability = (Ability) pobject;
			if (rootArrayList.contains(item))
			{
				for (Iterator e2 = Globals.getSkillList().iterator(); e2.hasNext();)
				{
					final Skill skill = (Skill) e2.next();

					if (skill.getRootName().equalsIgnoreCase(item))
					{
						ability.addCcSkill(skill.getName());
					}
				}
			}
			else
			{
				ability.addCcSkill(item);
			}
		}
	}

}
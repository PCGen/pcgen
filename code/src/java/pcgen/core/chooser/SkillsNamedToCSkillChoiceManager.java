/**
 * SkillsNamedChoiceManager.java
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

import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

/**
 * This is the chooser that deals with choosing a skill.
 */
public class SkillsNamedToCSkillChoiceManager extends SkillsNamedChoiceManager {

	/**
	 * Make a new named skills chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public SkillsNamedToCSkillChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
	}

	@Override
	protected void associateChoice(PlayerCharacter pc, String st)
	{
		super.associateChoice(pc, st);
		if (pobject != null && pobject instanceof Ability)
		{
			pobject.addCSkill(st);
		}
	}

	/**
	 * If pobject is an Ability object, clean up the list of Class skill associated
	 * with it.
	 *
	 * @param aPc
	 */
	@Override
	protected void cleanUpAssociated(PlayerCharacter aPC)
	{
		if (pobject != null && pobject instanceof Ability)
		{
			Ability anAbility = (Ability) pobject;

			List<String> skillList = anAbility.getCSkillList();
			if (skillList != null)
			{
				Ability globalAbility = Globals.getAbilityKeyed(anAbility
						.getCategory(), pobject.getKeyName());
				List<String> globalList = globalAbility.getCSkillList();
				anAbility.clearCSkills();
				if (globalList != null)
				{
					skillList.retainAll(globalList);
					for (String keepMe : skillList)
					{
						anAbility.addCSkill(keepMe);
					}
				}
			}
		}
		super.cleanUpAssociated(aPC);
	}
}

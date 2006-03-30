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

import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

import java.util.Iterator;

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
		title = "Skills Choice";
		chooserHandled = "SKILLSNAMEDTOCSKILL";
		
		if (choices != null && choices.size() > 0 &&
				((String) choices.get(0)).equals(chooserHandled)) {
			choices = choices.subList(1, choices.size());
		}
	}

	/**
	 * If pobject is an Ability object, clean up the list of Class skill associated
	 * with it.
     *
	 * @param aPc
	 */
	protected void cleanUpAssociated(
			PlayerCharacter aPc, int size)
	{
		if (pobject != null && pobject instanceof Ability)
		{
			Ability anAbility = (Ability) pobject;
			
			for (Iterator cSkillIt = anAbility.getCSkillList().iterator(); cSkillIt.hasNext();)
			{
				final String tempString = (String) cSkillIt.next();

				if (!"LIST".equals(tempString))
				{
					String tempName = pobject.getName();
					final Ability tempAbility = Globals.getAbilityNamed("FEAT", tempName);

					if (tempAbility != null)
					{
						if (tempAbility.getCSkillList() != null)
						{
							if (tempAbility.getCSkillList().contains(tempString))
							{
								cSkillIt.remove();
							}
						}
					}
				}
			}

			anAbility.clearCcSkills();
		}
		
		super.cleanUpAssociated(aPc, size);
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
			pobject.addCcSkill(item);
		}
	}

}

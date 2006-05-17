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

import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;

import java.util.Iterator;
import java.util.List;

/**
 * This is the chooser that deals with choosing a skill.
 */
public class SkillsNamedChoiceManager extends AbstractComplexChoiceManager {

	/**
	 * Make a new named skills chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public SkillsNamedChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		title = "Skills Choice";
		chooserHandled = "SKILLSNAMED";

		if (choices != null && choices.size() > 0 &&
				((String) choices.get(0)).equals(chooserHandled)) {
			choices = choices.subList(1, choices.size());
		}
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	public void getChoices(
			final PlayerCharacter aPc,
			final List            availableList,
			final List            selectedList)
	{
		Iterator choiceIt = choices.iterator();

		while (choiceIt.hasNext())
		{
			String token = (String) choiceIt.next();
			boolean startsWith = false;

			if (token.startsWith("TYPE.") || token.startsWith("TYPE="))
			{
				Skill aSkill;

				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();

					if (aSkill.isType(token.substring(5)))
					{
						availableList.add(aSkill.getKeyName());
					}
				}
			}

			if ("ALL".equals(token))
			{
				Skill aSkill;

				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();
					availableList.add(aSkill.getKeyName());
				}
			}

			if ("CLASS".equals(token))
			{
				Skill aSkill;

				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();

					if (aSkill.costForPCClassList(aPc.getClassList(), aPc) == Globals.getGameModeSkillCost_Class())
					{
						availableList.add(aSkill.getKeyName());
					}
				}
			}

			if ("CROSSCLASS".equals(token))
			{
				Skill aSkill;

				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();

					if (aSkill.costForPCClassList(aPc.getClassList(), aPc) > Globals.getGameModeSkillCost_Class())
					{
						availableList.add(aSkill.getKeyName());
					}
				}
			}

			if ("EXCLUSIVE".equals(token))
			{
				Skill aSkill;

				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();

					if (aSkill.costForPCClassList(aPc.getClassList(), aPc) == Globals.getGameModeSkillCost_Exclusive())
					{
						availableList.add(aSkill.getKeyName());
					}
				}
			}

			if ("NORANK".equals(token))
			{
				Skill aSkill;
				Skill pcSkill;

				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();
					pcSkill = aPc.getSkillKeyed(aSkill.getKeyName());

					if (pcSkill == null || Double.compare(pcSkill.getRank().doubleValue(), 0.0) == 0)
					{
						availableList.add(aSkill.getKeyName());
					}
				}
			}

			if (token.endsWith("%"))
			{
				startsWith = true;
				token = token.substring(0, token.length() - 1);
			}

			Skill aSkill;

			for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
			{
				aSkill = (Skill) e1.next();

				if (aSkill.getKeyName().equals(token) || (startsWith && aSkill.getKeyName().startsWith(token)))
				{
					availableList.add(aSkill.getKeyName());
				}
			}
		}

		pobject.addAssociatedTo(selectedList);
	}


}

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

import java.util.Collection;
import java.util.List;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.util.enumeration.Visibility;

/**
 * This is the chooser that deals with choosing a skill.
 */
public class SkillsNamedChoiceManager extends
		AbstractEasyStringChoiceManager<Skill>
{

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
		setTitle("Skills Choice");
	}

	@Override
	public Collection<Skill> getAllObjects()
	{
		return Globals.getPartialSkillList(Visibility.DISPLAY_ONLY);
	}

	@Override
	public Skill getSpecificObject(String key)
	{
		return Globals.getSkillKeyed(key);
	}

	@Override
	protected void processOther(String token, List<String> availableList,
			PlayerCharacter aPc)
	{
		if ("CLASS".equals(token))
		{
			for (Skill skill : getAllObjects())
			{
				if (skill.costForPCClassList(aPc.getClassList(), aPc) == Globals
						.getGameModeSkillCost_Class())
				{
					availableList.add(skill.getKeyName());
				}
			}
		}
		else if ("CROSSCLASS".equals(token))
		{
			for (Skill skill : getAllObjects())
			{
				if (skill.costForPCClassList(aPc.getClassList(), aPc) > Globals
						.getGameModeSkillCost_Class())
				{
					availableList.add(skill.getKeyName());
				}
			}
		}
		else if ("EXCLUSIVE".equals(token))
		{
			for (Skill skill : getAllObjects())
			{
				if (skill.costForPCClassList(aPc.getClassList(), aPc) == Globals
						.getGameModeSkillCost_Exclusive())
				{
					availableList.add(skill.getKeyName());
				}
			}
		}
		else if ("NORANK".equals(token))
		{
			for (Skill skill : getAllObjects())
			{
				final Skill pcSkill = aPc.getSkillKeyed(skill.getKeyName());

				if (pcSkill == null
						|| Double.compare(pcSkill.getRank().doubleValue(), 0.0) == 0)
				{
					availableList.add(skill.getKeyName());
				}
			}
		}
		else if (token.startsWith("RANKS="))
		{
			Double ranks = new Double(token.substring(6));
			for (Skill skill : getAllObjects())
			{
				final Skill pcSkill = aPc.getSkillKeyed(skill.getKeyName());

				if (pcSkill != null
						&& Double.compare(pcSkill.getRank().doubleValue(),
								ranks) >= 0)
				{
					availableList.add(skill.getKeyName());
				}
			}
		}
		else if (token.endsWith("%"))
		{
			token = token.substring(0, token.length() - 1);
			for (Skill skill : getAllObjects())
			{
				if (skill.getKeyName().startsWith(token))
				{
					availableList.add(skill.getKeyName());
				}
			}
		}
		else
		{
			super.processOther(token, availableList, aPc);
		}
	}
}

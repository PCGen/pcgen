/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.helper;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillRankControl;

public class ClassSkillChoiceActor implements PersistentChoiceActor<Skill>
{

	private final PCClass source;
	private final Integer applyRank;

	public ClassSkillChoiceActor(PCClass obj, Integer autoRank)
	{
		applyRank = autoRank;
		source = obj;
	}

	public void applyChoice(CDOMObject owner, Skill choice, PlayerCharacter pc)
	{
		Skill pcSkill = pc.addSkill(choice);
		PCClass pcc = pc.getClassKeyed(source.getKeyName());
		pc.addAssoc(pcc, AssociationListKey.CSKILL, pcSkill);
		if (applyRank != null)
		{
			SkillRankControl.modRanks(applyRank, pcc, false, pc, pcSkill);
		}
	}

	public boolean allow(Skill choice, PlayerCharacter pc, boolean allowStack)
	{
		return !pc.isClassSkill(choice);
	}

	public Skill decodeChoice(String s)
	{
		return Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				Skill.class, s);
	}

	public String encodeChoice(Object choice)
	{
		return ((Skill) choice).getKeyName();
	}

	public void restoreChoice(PlayerCharacter pc, CDOMObject owner, Skill choice)
	{
		Skill pcSkill = pc.addSkill(choice);
		PCClass pcc = pc.getClassKeyed(source.getKeyName());
		pc.addAssoc(pcc, AssociationListKey.CSKILL, pcSkill);
	}

	public Integer getApplyRank()
	{
		return applyRank;
	}

}

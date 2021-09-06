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
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.SubClass;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

/**
 * A ClassSkillChoiceActor is a PersistentChoiceActor that can apply skill
 * selections that are related to a specific PCClass (this adds the selected
 * skill(s) as Class Skills) and which may apply a number of ranks to the
 * selected skill.
 */
public class ClassSkillChoiceActor implements PersistentChoiceActor<Skill>
{

	/**
	 * The PCClass to which skill selections will be applied as a class skill
	 */
	private final PCClass source;

	/**
	 * The number of ranks that this ClassSkillChoiceActor should apply to the
	 * selected skills. May be null if this ClassSkillChoiceActor should not
	 * apply any ranks to the selected skills.
	 */
	private final Integer applyRank;

	/**
	 * Constructs a new ClassSkillChoiceActor which will apply skill selections
	 * to the given class, and automatically apply the given ranks to selected
	 * skills.
	 * 
	 * @param pcc
	 *            The PCClass to which skill selections will be applied as a
	 *            class skill
	 * @param autoRank
	 *            The number of ranks that this ClassSkillChoiceActor should
	 *            apply to selected skills.
	 */
	public ClassSkillChoiceActor(PCClass pcc, Integer autoRank)
	{
		applyRank = autoRank;
		source = pcc;
	}

	/**
	 * Applies the given Skill choice to the given PlayerCharacter. The given
	 * Skill is added as a class skill for the PCClass provided during
	 * construction of this ClassSkillChoiceActor. If the number of ranks
	 * provided during construction of this ClassSkillChoiceActor was not null
	 * or zero, then ranks are also applied to the given skill.
	 * 
	 * @param owner
	 *            The owning object for this choice.
	 * @param choice
	 *            The Skill which should be added as a Class Skill for the
	 *            PCClass provided during construction of this
	 *            ClassSkillChoiceActor
	 * @param pc
	 *            The PlayerCharacter to which the changes driven by this
	 *            ClassSkillChoiceActor should be applied.
	 */
	@Override
	public void applyChoice(CDOMObject owner, Skill choice, PlayerCharacter pc)
	{
		PCClass pcc = getSourceClass(pc);
		if (pcc == null)
		{
			Logging.errorPrint("Unable to find the pc's class " + source + " to apply skill choices to.");
			return;
		}
		pc.addLocalCost(pcc, choice, SkillCost.CLASS, owner);
		if (applyRank != null)
		{
			if (owner instanceof PCClassLevel classLevel)
			{
				// Ensure that the skill points for this level are already calculated.
				PCClass pcClass = (PCClass) classLevel.getSafe(ObjectKey.PARENT);

				int levelIndex = 1;
				for (PCLevelInfo lvlInfo : pc.getLevelInfo())
				{
					if (lvlInfo.getClassKeyName().equals(pcClass.getKeyName())
						&& lvlInfo.getClassLevel() == classLevel.getSafe(IntegerKey.LEVEL))
					{
						pc.checkSkillModChangeForLevel(pcClass, lvlInfo, classLevel, levelIndex++);
						break;
					}
				}
			}
			String result = SkillRankControl.modRanks(applyRank, pcc, false, pc, choice);
			if (StringUtils.isNotEmpty(result))
			{
				Logging.errorPrint("Unable to apply {0} ranks of {1}. Error: {2}", applyRank, choice, result);
			}
		}
	}

	/**
	 * Returns true if the given Skill should be allowed as a selection when
	 * determining which Skills can be added as a Class Skill to the PCClass in
	 * this ClassSkillChoiceActor for the given PlayerCharacter. Generally, this
	 * is used to filter out Skills that are already class skills for the
	 * PCClass in this ClassSkillChoiceActor.
	 * 
	 * @param choice
	 *            The Skill to be tested to see if selection of the Skill should
	 *            be allowed for this ClassSkillChoiceActor.
	 * @param pc
	 *            The PlayerCharacter to which the changes driven by this
	 *            ClassSkillChoiceActor will be applied.
	 * @param allowStack
	 *            True if stacking is allowed (ignored by ClassSkillChoiceActor)
	 * 
	 * @return true if the given Skill should be allowed as a selection.
	 */
	@Override
	public boolean allow(Skill choice, PlayerCharacter pc, boolean allowStack)
	{
		return !pc.isClassSkill(source, choice);
	}

	/**
	 * Decodes the given String into a Skill. The String format to be passed
	 * into this method is defined solely by the return result of the
	 * encodeChoice method. There is no guarantee that the encoding is human
	 * readable, simply that the encoding is uniquely identifying such that this
	 * method is capable of decoding the String into the Skill.
	 * @param persistentFormat
	 *            The String which should be decoded to provide a Skill.
	 * 
	 * @return A Skill that was encoded in the given String.
	 */
	@Override
	public Skill decodeChoice(LoadContext context, String persistentFormat)
	{
		return context.getReferenceContext().silentlyGetConstructedCDOMObject(Skill.class, persistentFormat);
	}

	/**
	 * Encodes the given Skill into a String sufficient to uniquely identify the
	 * Skill. This may not sufficiently encode to be stored into a file or
	 * format which restricts certain characters (such as URLs), it simply
	 * encodes into an identifying String. There is no guarantee that this
	 * encoding is human readable, simply that the encoding is uniquely
	 * identifying such that the decodeChoice method of ClassSkillChoiceActor is
	 * capable of decoding the String into the Skill.
	 * 
	 * @param choice
	 *            The Skill which should be encoded into a String sufficient to
	 *            identify the Skill.
	 * 
	 * @return A String sufficient to uniquely identify the Skill.
	 */
	@Override
	public String encodeChoice(Skill choice)
	{
		return choice.getKeyName();
	}

	/**
	 * Restores a choice to a PlayerCharacter. This method re-applies a Skill
	 * when a PlayerCharacter is restored from a persistent state (the
	 * applyChoice method of ClassSkillChoiceActor having been used to first
	 * apply the choice to a PlayerCharacter).
	 * 
	 * @param pc
	 *            The PlayerCharacter to which the Skill should be restored.
	 * @param owner
	 *            The owning object of the Skill being restored.
	 * @param choice
	 *            The Skill being restored to the given PlayerCharacter.
	 */
	@Override
	public void restoreChoice(PlayerCharacter pc, CDOMObject owner, Skill choice)
	{
		PCClass pcc = getSourceClass(pc);
		if (pcc == null)
		{
			Logging.errorPrint("Unable to find the pc's class " + source + " to restore skill choices to.");
			return;
		}
		pc.addLocalCost(pcc, choice, SkillCost.CLASS, owner);
	}

	/**
	 * Identify the character's instance of the class being linked to the skill. 
	 * @param pc The character
	 * @return The character's class.
	 */
	private PCClass getSourceClass(PlayerCharacter pc)
	{
		PCClass pcc;
		if (source instanceof SubClass)
		{
			pcc = pc.getClassKeyed(((SubClass) source).getCDOMCategory().getKeyName());
		}
		else
		{
			pcc = pc.getClassKeyed(source.getKeyName());
		}
		return pcc;
	}

	/**
	 * Returns the number of ranks that this ClassSkillChoiceActor should apply
	 * to the selected skills. May be null if this ClassSkillChoiceActor should
	 * not apply any ranks to the selected skills.
	 * 
	 * @return The number of ranks that this ClassSkillChoiceActor should apply
	 *         to the selected skills.
	 */
	public Integer getApplyRank()
	{
		return applyRank;
	}

	/**
	 * Removes a Skill choice from a PlayerCharacter.
	 * 
	 * @param pc
	 *            The PlayerCharacter from which the Skill should be removed.
	 * @param owner
	 *            The owning object of the Skill being removed.
	 * @param choice
	 *            The Skill being removed from the given PlayerCharacter.
	 */
	@Override
	public void removeChoice(PlayerCharacter pc, CDOMObject owner, Skill choice)
	{
		PCClass pcc = pc.getClassKeyed(source.getKeyName());
		if (applyRank != null)
		{
			SkillRankControl.modRanks(-applyRank, pcc, false, pc, choice);
		}
		pc.removeLocalCost(pcc, choice, SkillCost.CLASS, owner);
	}
}

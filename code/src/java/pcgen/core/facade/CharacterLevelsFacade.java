/*
 * CharacterLevelsFacade.java
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on May 31, 2010, 4:33:45 PM
 */
package pcgen.core.facade;

import java.util.EventListener;
import java.util.EventObject;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.facade.util.ListFacade;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public interface CharacterLevelsFacade extends ListFacade<CharacterLevelFacade>
{

	/**
	 * This method handles adding and removing skill points to the character's
	 * skills. This methods takes into acount the skill cost and spendable skill
	 * points and will call appropriate message dialogs when an inappropriate
	 * action is called.
	 * @param level the level to add these skill points to
	 * @param skill the skill to invest points in
	 * @param points the amount of points to invest
	 * @return true if the points were successfuly invested
	 */
	public boolean investSkillPoints(CharacterLevelFacade level, SkillFacade skill, int points);

	public ClassFacade getClassTaken(CharacterLevelFacade level);

	public SkillCost getSkillCost(CharacterLevelFacade level, SkillFacade skill);

	public int getRankCost(CharacterLevelFacade level, SkillCost cost);

	public int getGainedSkillPoints(CharacterLevelFacade level);

	public void setGainedSkillPoints(CharacterLevelFacade level, int points);

	public int getSpentSkillPoints(CharacterLevelFacade level);

	public int getHPGained(CharacterLevelFacade level);

	public int getHPRolled(CharacterLevelFacade level);

	public void setHPRolled(CharacterLevelFacade level, int hp);

	public int getSkillTotal(CharacterLevelFacade level, SkillFacade skill);

	public int getSkillModifier(CharacterLevelFacade level, SkillFacade skill);

	public float getSkillRanks(CharacterLevelFacade level, SkillFacade skill);

	public float getMaxRanks(CharacterLevelFacade level, SkillCost cost);

	void addClassListener(ClassListener listener);

	void removeClassListener(ClassListener listener);

	void addHitPointListener(HitPointListener listener);

	void removeHitPointListener(HitPointListener listener);

	void addSkillBonusListener(SkillBonusListener listener);

	void removeSkillBonusListener(SkillBonusListener listener);

	void addSkillPointListener(SkillPointListener listener);

	void removeSkillPointListener(SkillPointListener listener);

	public static interface ClassListener extends EventListener
	{

		void classChanged(CharacterLevelEvent e);

	}

	public static interface HitPointListener extends EventListener
	{

		void hitPointsChanged(CharacterLevelEvent e);

	}

	public static interface SkillBonusListener extends EventListener
	{

		void skillBonusChanged(CharacterLevelEvent e);

	}

	public static interface SkillPointListener extends EventListener
	{

		void skillPointsChanged(CharacterLevelEvent e);

	}

	public static class CharacterLevelEvent extends EventObject
	{

		private int baseLevel;
		private boolean stacks;

		public CharacterLevelEvent(Object source, int baseLevel, boolean stacks)
		{
			super(source);
			this.baseLevel = baseLevel;
			this.stacks = stacks;
		}

		public int getBaseLevelIndex()
		{
			return baseLevel;
		}

		/**
		 * @return whether this event affects values at higher levels
		 */
		public boolean affectsHigherLevels()
		{
			return stacks;
		}

	}

}

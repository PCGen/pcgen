/*
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
 */
package pcgen.facade.core;

import java.util.EventListener;
import java.util.EventObject;

import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.PCClass;
import pcgen.core.Skill;
import pcgen.facade.util.ListFacade;

public interface CharacterLevelsFacade extends ListFacade<CharacterLevelFacade>
{

    /**
     * This method handles adding and removing skill points to the character's
     * skills. This methods takes into acount the skill cost and spendable skill
     * points and will call appropriate message dialogs when an inappropriate
     * action is called.
     *
     * @param level  the level to add these skill points to
     * @param skill  the skill to invest points in
     * @param points the amount of points to invest
     * @return true if the points were successfuly invested
     */
    boolean investSkillPoints(CharacterLevelFacade level, Skill skill, int points);

    PCClass getClassTaken(CharacterLevelFacade level);

    SkillCost getSkillCost(CharacterLevelFacade level, Skill skill);

    int getGainedSkillPoints(CharacterLevelFacade level);

    void setGainedSkillPoints(CharacterLevelFacade level, int points);

    int getSpentSkillPoints(CharacterLevelFacade level);

    /**
     * Return the number of skills points remaining to be spent for the level.
     *
     * @param level The character level to be checked.
     * @return The number of skills points left.
     */
    int getRemainingSkillPoints(CharacterLevelFacade level);

    int getHPGained(CharacterLevelFacade level);

    int getHPRolled(CharacterLevelFacade level);

    void setHPRolled(CharacterLevelFacade level, int hp);

    float getSkillRanks(CharacterLevelFacade level, Skill skill);

    /**
     * Retrieve a breakdown of the skill details at a particular level.
     *
     * @param level The level to retrieve.
     * @param skill The skill to retrieve
     * @return A SkillBreakdown containing the modifier, ranks and total for the skill at the level.
     */
    SkillBreakdown getSkillBreakdown(CharacterLevelFacade level, Skill skill);

    /**
     * Retrieve the maximum number of ranks the character may have in a skill at a level.
     *
     * @param level              The character level to be checked..
     * @param cost               The cost at which the skill rank would be purchased.
     * @param isClassForMaxRanks Has the skill been a class skill at this or an earlier level.
     * @return The maximum allowed ranks.
     */
    float getMaxRanks(CharacterLevelFacade level, SkillCost cost, boolean isClassForMaxRanks);

    /**
     * Check if the skill is class for max ranks purposes as at the specified level.
     * A skill is class for max ranks purposes if it has ever been class for the
     * character up to the level.
     *
     * @param level The level at which to check.
     * @param skill The skill to be checked.
     * @return True if the skill should be treated as class.
     */
    boolean isClassSkillForMaxRanks(CharacterLevelFacade level, Skill skill);

    void addClassListener(ClassListener listener);

    void addHitPointListener(HitPointListener listener);

    void removeHitPointListener(HitPointListener listener);

    void addSkillBonusListener(SkillBonusListener listener);

    void removeSkillBonusListener(SkillBonusListener listener);

    void addSkillPointListener(SkillPointListener listener);

    void removeSkillPointListener(SkillPointListener listener);

    /**
     * Identify the appropriate target level for setting the skill to a
     * value.
     *
     * @param skill     The skill being changed.
     * @param baseLevel The level at which the user has requested the change.
     * @param ranks     The new number of ranks.
     * @return The recommended level.
     */
    CharacterLevelFacade findNextLevelForSkill(Skill skill, CharacterLevelFacade baseLevel, float ranks);

    @FunctionalInterface
    interface ClassListener extends EventListener
    {

        void classChanged(CharacterLevelEvent e);

    }

    @FunctionalInterface
    interface HitPointListener extends EventListener
    {

        void hitPointsChanged(CharacterLevelEvent e);

    }

    @FunctionalInterface
    interface SkillBonusListener extends EventListener
    {

        void skillBonusChanged(CharacterLevelEvent e);

    }

    @FunctionalInterface
    interface SkillPointListener extends EventListener
    {

        void skillPointsChanged(CharacterLevelEvent e);

    }

    class CharacterLevelEvent extends EventObject
    {

        private final int baseLevel;
        private final boolean stacks;

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

    /**
     * The Class {@code SkillBreakdown} holds the modifier, rank and total
     * for a skill.
     */
    class SkillBreakdown
    {
        public float ranks = 0.0f;
        public int modifier = 0;
        public int total = 0;
    }

}

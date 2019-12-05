/*
 * Copyright 2002 (C) James Dempsey
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
 */
package pcgen.core;

import java.math.BigDecimal;

import pcgen.util.Logging;

/**
 * {@code LevelInfo} describes the data associated with a level
 */
public final class LevelInfo
{
    private String maxClassSkillString = "0";
    private String maxCrossClassSkillString = "0";
    private String levelString = "0";
    private String minXPString = "0";

    /**
     * Sets the levelString that this LevelInfo object describes.
     *
     * @param arg The level to be set (should be a number or variable)
     */
    public void setLevelString(final String arg)
    {
        levelString = arg;
    }

    /**
     * Gets the levelString that this LevelInfo object describes.
     *
     * @return level string
     */
    public String getLevelString()
    {
        return levelString;
    }

    /**
     * Sets the maximum number of ranks a character can have in
     * a class skill when they are at the level that this LevelInfo
     * object describes.
     *
     * @param arg The maximum number of ranks allowed for a class skill
     *            should be a number or a formula based on variable defined
     *            in setLevelString.
     */
    public void setMaxClassSkillString(final String arg)
    {
        maxClassSkillString = arg;
    }

    /**
     * Sets the maximum number of ranks a character can have in
     * a cross-class skill when they are at the level that this LevelInfo
     * object describes.
     *
     * @param arg The maximum number of ranks allowed for a cross-class skill
     *            should be a number or a formula based on variable defined
     *            in setLevelString.
     */
    public void setMaxCrossClassSkillString(final String arg)
    {
        maxCrossClassSkillString = arg;
    }

    /**
     * Sets the min number of experience points required to
     * qualify for the level that this LevelInfo object describes
     *
     * @param arg The amount of experience needed to acquire this level
     *            should be a number or a formula based on variable defined
     *            in setLevelString.
     **/
    public void setMinXPString(final String arg)
    {
        minXPString = arg;
    }

    /**
     * Retrieves a human readable description of the details of this
     * LevelInfo object.
     *
     * @return A string describing the LevelInfo object.
     */
    @Override
    public String toString()
    {
        return "Level: " + levelString + " MinXP: " + minXPString + " MaxClassSkill: " + maxClassSkillString
                + " MaxCrossClassSkill: " + maxCrossClassSkillString + ".";
    }

    /**
     * Retrieves the variable indicating the min number of experience points
     * required to qualify for the level that this LevelInfo object describes
     *
     * @param levelArg
     * @return XP point value variable
     */
    public String getMinXPVariable(final int levelArg)
    {
        return minXPString.replaceAll(levelString, String.valueOf(levelArg));
    }

    /**
     * Retrieves the maximum number of ranks a character can have in
     * a class skill when they are at the level that this LevelInfo
     * object describes.
     *
     * @param levelArg
     * @param calcPC
     * @return The maximum number of ranks allowed for a class skill
     */
    public BigDecimal getMaxClassSkillRank(final int levelArg, final PlayerCharacter calcPC)
    {
        return getMaxSkillRank(levelArg, calcPC, maxClassSkillString);
    }

    /**
     * Retrieves the maximum number of ranks a character can have in
     * a cross class skill when they are at the level that this LevelInfo
     * object describes.
     *
     * @param levelArg
     * @param calcPC
     * @return The maximum number of ranks allowed for a cross-class skill
     */
    public BigDecimal getMaxCrossClassSkillRank(final int levelArg, final PlayerCharacter calcPC)
    {
        return getMaxSkillRank(levelArg, calcPC, maxCrossClassSkillString);
    }

    /**
     * Retrieves the maximum number of ranks a character can have in
     * a skill of the passed in type when they are at the level that this LevelInfo
     * object describes.
     *
     * @param levelArg
     * @param calcPC
     * @param maxSkillString
     * @return The maximum number of ranks allowed for a skill of the passed in type
     */
    private BigDecimal getMaxSkillRank(final int levelArg, final PlayerCharacter calcPC, final String maxSkillString)
    {
        double ranks;
        if (calcPC == null)
        {
            return BigDecimal.ZERO;
        }
        try
        {
            ranks = calcPC.getVariableValue(maxSkillString.replaceAll(levelString, String.valueOf(levelArg)), "")
                    .doubleValue();
        } catch (Exception e)
        {
            // Why fetch the value twice?
            Logging.errorPrint("Exception while getting max rank for " + levelString, e);
            //final String rankString = maxSkillString.replaceAll(levelString, String.valueOf(levelArg));
            //ranks = calcPC.getVariableValue(rankString, "").doubleValue();
            ranks = 0.0d;
        }

        return new BigDecimal(String.valueOf(ranks));
    }

}

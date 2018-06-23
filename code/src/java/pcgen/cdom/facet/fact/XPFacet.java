/*
 * Copyright (c) Thomas Parker, 2009.
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
package pcgen.cdom.facet.fact;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.analysis.LevelFacet;
import pcgen.cdom.facet.analysis.LevelTableFacet;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.util.Logging;

/**
 * XP Facet is a facet that tracks the Experience Points of a Player Character.
 * 
 * Earned Experience Points are Experience Points that the Player Character has
 * earned through play.
 * 
 * Level-Adjusted Experience Points are Experience Points that the Player
 * Character has received through level adjustments, and are independent of
 * earned Experience Points.
 * 
 * Total Experience Points are a combination of Earned Experience Points and
 * Level-Adjusted Experience Points.
 */
public class XPFacet extends AbstractItemFacet<CharID, Integer>
{

	private LevelFacet levelFacet;
	private LevelTableFacet levelTableFacet;

	/**
	 * Sets the number of earned Experience Points for the Player Character
	 * represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            earned Experience Points will be set.
	 * @param earnedXP
	 *            The earned Experience Points for the Player Character
	 *            represented by the given CharID
	 * @return
	 * 			  true if the number of earned Experience Points was set; false otherwise
	 */
	public boolean setEarnedXP(CharID id, int earnedXP)
	{
		return set(id, earnedXP);
	}

	/**
	 * Returns the earned Experience Points for the Player Character represented
	 * by the given CharID.
	 * 
	 * @param id
	 *            The Player Character for which the earned Experience Points
	 *            will be returned
	 * @return The earned Experience Points for the Player Character represented
	 *         by the given CharID
	 */
	public int getEarnedXP(CharID id)
	{
		Integer earnedXP = this.get(id);
		return earnedXP == null ? 0 : earnedXP;
	}

	/**
	 * Return the total Experience Points for the Player Character represented
	 * by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the PlayerCharacter for which the
	 *            total Experience Points will be returned
	 * @return The total Experience Points for the Player Character represented
	 *         by the given CharID
	 */
	public int getXP(CharID id)
	{
		// Add the effect of LEVELADJ when showing our external notion of XP.
		return getEarnedXP(id) + getLAXP(id);
	}

	/**
	 * Returns the level-adjusted Experience Points for the Player Character
	 * represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            level-adjusted Experience Points will be returned
	 * @return The level-adjusted Experience Points for the Player Character
	 *         represented by the given CharID
	 */
	private int getLAXP(CharID id)
	{
		/*
		 * Why +1? Adjustments are deltas, not absolute levels, so are not
		 * subject to the "back off one" element of the algorithm in
		 * minXPForLevel. This still means that levelAdjustment of 0 gives you 0
		 * XP, but we need LA of 1 to give us 1,000 XP.
		 */
		return levelTableFacet.minXPForLevel(levelFacet.getLevelAdjustment(id) + 1, id);
	}

	/**
	 * Sets the total Experience Points for the Player Character represented by
	 * the given CharID to the given value.
	 * 
	 * Note this sets earned Experience Points as a side effect (calculated
	 * based on the level-adjusted Experience Points the Player Character may
	 * have). If the given xp value is less than the level-adjusted Experience
	 * Points possessed by the Player Character, then an error will be logged,
	 * and the earned Experience Points will be set to 0.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            total Experience Points will be set
	 * @param xp
	 *            The total Experience Points for the Player Character
	 *            represented by the given CharID
	 * @return
	 * 			  true if the total Experience Points was set; false otherwise
	 */
	public boolean setXP(CharID id, int xp)
	{
		// Remove the effect of LEVELADJ when storing our
		// internal notion of experience
		int realXP = xp - getLAXP(id);

		if (realXP < 0)
		{
			Logging.errorPrint("ERROR: too little experience: " + realXP);
			realXP = 0;
		}

		return setEarnedXP(id, realXP);
	}

	/**
	 * @param levelFacet the levelFacet to set
	 */
	public void setLevelFacet(LevelFacet levelFacet)
	{
		this.levelFacet = levelFacet;
	}

	/**
	 * @param levelTableFacet the levelTableFacet to set
	 */
	public void setLevelTableFacet(LevelTableFacet levelTableFacet)
	{
		this.levelTableFacet = levelTableFacet;
	}

}

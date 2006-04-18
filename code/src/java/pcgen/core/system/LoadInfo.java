/*
 * LevelInfo.java
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
 *
 * Created on August 16, 2002, 10:00 PM AEST (+10:00)
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code>LoadInfo</code> describes the data associated with a loads and encumbrance
 *
 * @author Stefan Radermacher <zaister@users.sourceforge.net>
 * @version $Revision$
 */
public class LoadInfo {
	private List loadScoreList = new ArrayList();
	private Float loadScoreMultiplier = new Float(0);
	private Map sizeAdjustmentMap = new HashMap();
	private Map loadMultiplierMap = new HashMap();
	private int minScore = 0;
	private int maxScore = 0;
	private String modifyFormula = "";

	/**
	 * Set the load score multiplier
	 * @param value
	 */
	public void setLoadScoreMultiplier(Float value)
	{
		loadScoreMultiplier = value;
	}

	/**
	 * Add a load score/value pair
	 * @param score
	 * @param value
	 */
	public void addLoadScoreValue(int score, Float value)
	{
		loadScoreList.add(score, value);
		if (score > maxScore)
		{
			maxScore = score;
		}
		if (score < maxScore)
		{
			minScore = score;
		}
	}

	/**
	 * Get the value for a load score
	 * @param score
	 * @return the value for a load score
	 */
	public Float getLoadScoreValue(int score)
	{
		if (score < minScore)
		{
			return new Float(0);
		}
		else if (score > maxScore)
		{
			if (getLoadMultiplierCount() == 1)
			{
				return getLoadScoreValue(minScore);
			}
			return new Float(loadScoreMultiplier.doubleValue() * getLoadScoreValue(score - 10).doubleValue());
		}
		else if (loadScoreList.get(score) == null)
		{
			return getLoadScoreValue(score - 1);
		}
		return (Float)loadScoreList.get(score);
	}

	/**
	 * Add a size adjustment
	 * @param size
	 * @param value
	 */
	public void addSizeAdjustment(String size, Float value)
	{
		sizeAdjustmentMap.put(size, value);
	}

	/**
	 * Get the size adjustment
	 * @param size
	 * @return the size adjustment
	 */
	public Float getSizeAdjustment(String size)
	{
		if (sizeAdjustmentMap.containsKey(size))
		{
			return (Float)sizeAdjustmentMap.get(size);
		}
		return null;
	}

	/**
	 * Add load multiplier
	 * @param encumbranceType
	 * @param value
	 * @param formula
	 * @param checkPenalty
	 */
	public void addLoadMultiplier(String encumbranceType, Float value, String formula, Integer checkPenalty)
	{
		LoadMapEntry newEntry = new LoadMapEntry(value, formula, checkPenalty);
		loadMultiplierMap.put(encumbranceType, newEntry);
	}

	/**
	 * Get the load multiplier
	 * @param encumbranceType
	 * @return load multiplier
	 */
	public Float getLoadMultiplier(String encumbranceType)
	{
		if (loadMultiplierMap.containsKey(encumbranceType))
		{
			return ((LoadMapEntry)loadMultiplierMap.get(encumbranceType)).getMuliplier();
		}
		return null;
	}

	/**
	 * Get the load move formula
	 * @param encumbranceType
	 * @return the load move formula
	 */
	public String getLoadMoveFormula(String encumbranceType)
	{
		if (loadMultiplierMap.containsKey(encumbranceType))
		{
			return ((LoadMapEntry)loadMultiplierMap.get(encumbranceType)).getFormula();
		}
		return "";
	}

	/**
	 * Get the load check penalty
	 * @param encumbranceType
	 * @return the load check penalty
	 */
	public int getLoadCheckPenalty(String encumbranceType)
	{
		if (loadMultiplierMap.containsKey(encumbranceType))
		{
			return ((LoadMapEntry)loadMultiplierMap.get(encumbranceType)).getCheckPenalty();
		}
		return 0;
	}

	/**
	 * Set the load modifier formula
	 * @param argFormula
	 */
	public void setLoadModifierFormula(final String argFormula)
	{
		modifyFormula = argFormula;
	}

	/**
	 * Get the load modifier formula
	 * @return the load modifier formula
	 */
	public String getLoadModifierFormula()
	{
		return modifyFormula;
	}

	/**
	 * Get the load multiplier count
	 * @return the load multiplier count
	 */
	public int getLoadMultiplierCount()
	{
		return loadMultiplierMap.size();
	}

	private class LoadMapEntry
	{
		private Float multiplier;
		private String moveFormula;
		private Integer checkPenalty;

		/**
		 * Constructor
		 * @param argMultiplier
		 * @param argFormula
		 * @param argPenalty
		 */
		public LoadMapEntry(Float argMultiplier, String argFormula,Integer argPenalty)
		{
			multiplier  = argMultiplier;
			moveFormula = argFormula;
			checkPenalty = argPenalty;
		}

		/**
		 * Get multiplier
		 * @return multiplier
		 */
		public Float getMuliplier()
		{
			return multiplier;
		}

		/**
		 * Get the formula
		 * @return formula
		 */
		public String getFormula()
		{
			return moveFormula;
		}

		/**
		 * Get the check penalty
		 * @return the check penalty
		 */
		public int getCheckPenalty()
		{
			return checkPenalty.intValue();
		}
	}

}

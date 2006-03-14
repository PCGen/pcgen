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

	public void addSizeAdjustment(String size, Float value)
	{
		sizeAdjustmentMap.put(size, value);
	}

	public Float getSizeAdjustment(String size)
	{
		if (sizeAdjustmentMap.containsKey(size))
		{
			return (Float)sizeAdjustmentMap.get(size);
		}
		return null;
	}

	public void addLoadMultiplier(String encumbranceType, Float value, String formula, Integer checkPenalty)
	{
		LoadMapEntry newEntry = new LoadMapEntry(value, formula, checkPenalty);
		loadMultiplierMap.put(encumbranceType, newEntry);
	}

	public Float getLoadMultiplier(String encumbranceType)
	{
		if (loadMultiplierMap.containsKey(encumbranceType))
		{
			return ((LoadMapEntry)loadMultiplierMap.get(encumbranceType)).getMuliplier();
		}
		return null;
	}

	public String getLoadMoveFormula(String encumbranceType)
	{
		if (loadMultiplierMap.containsKey(encumbranceType))
		{
			return ((LoadMapEntry)loadMultiplierMap.get(encumbranceType)).getFormula();
		}
		return "";
	}

	public int getLoadCheckPenalty(String encumbranceType)
	{
		if (loadMultiplierMap.containsKey(encumbranceType))
		{
			return ((LoadMapEntry)loadMultiplierMap.get(encumbranceType)).getCheckPenalty();
		}
		return 0;
	}

	public void setLoadModifierFormula(final String argFormula)
	{
		modifyFormula = argFormula;
	}

	public String getLoadModifierFormula()
	{
		return modifyFormula;
	}

	public int getLoadMultiplierCount()
	{
		return loadMultiplierMap.size();
	}

	private class LoadMapEntry
	{
		private Float multiplier;
		private String moveFormula;
		private Integer checkPenalty;

		public LoadMapEntry(Float argMultiplier, String argFormula,Integer argPenalty)
		{
			multiplier  = argMultiplier;
			moveFormula = argFormula;
			checkPenalty = argPenalty;
		}

		public Float getMuliplier()
		{
			return multiplier;
		}

		public String getFormula()
		{
			return moveFormula;
		}

		public int getCheckPenalty()
		{
			return checkPenalty.intValue();
		}
	}

}

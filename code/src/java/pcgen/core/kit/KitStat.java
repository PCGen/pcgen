/*
 * KitStat.java
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on September 20, 2005, 1040h
 *
 * $Id$
 */
package pcgen.core.kit;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Kit;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.StatList;
import pcgen.core.pclevelinfo.PCLevelInfo;

/**
 * KitStat
 *
 * @author boomer70
 */
public class KitStat extends BaseKit implements Serializable, Cloneable
{
	// Only change the UID when the serialized form of the class has also changed
	private static final long serialVersionUID = 1;

	private String theStatName = null;
	private String theStatValue = "";

	private transient PCStat theStat = null;

	/**
	 * Constructor
	 * @param aStatName
	 * @param aStatValue
	 */
	public KitStat(final String aStatName, final String aStatValue)
	{
		theStatName = aStatName;
		theStatValue = aStatValue;
	}

	/**
	 * Get the name of the stat
	 * @return the name of the stat
	 */
	public String getStatName()
	{
		return theStatName;
	}

	public String toString()
	{
		StringBuffer ret = new StringBuffer(100);
		ret.append(theStatName).append("=").append(theStatValue);
		return ret.toString();
	}

	public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
	{
		boolean foundStat = false;
		int sVal = aPC.getVariableValue(theStatValue,"").intValue();

		final StatList statList = aPC.getStatList();
		for ( int i = 0; i < statList.size(); i++ )
		{
			final PCStat currentStat = statList.getStatAt(i);
			if ( !aPC.isNonAbility(i) &&
				 currentStat.getAbb().equals(getStatName()) )
			{
				currentStat.setBaseScore(sVal);
				theStat = currentStat.clone();
				foundStat = true;
				if ("INT".equals(currentStat.getAbb()))
				{
					recalculateSkillPoints(aPC);
				}
				break;
			}
		}

		if (!foundStat && warnings != null)
		{
			warnings.add("STAT: Could not find stat \"" + getStatName()
						 + "\"");
		}

		return false;
	}

	public void apply(PlayerCharacter aPC)
	{
		final StatList aStatList = aPC.getStatList();
		for (Iterator<PCStat> stat = aStatList.iterator(); stat.hasNext();)
		{
			final PCStat currentStat = stat.next();
			if (currentStat.getAbb().equals(theStat.getAbb()))
			{
				currentStat.setBaseScore(theStat.getBaseScore());
				if ("INT".equals(currentStat.getAbb()))
				{
					recalculateSkillPoints(aPC);
				}
				break;
			}
		}
	}

	public String getObjectName()
	{
		return "Stats";
	}

	private void recalculateSkillPoints(PlayerCharacter aPC)
	{
		final List<PCClass> classes = aPC.getClassList();
		aPC.calcActiveBonuses();
		if (classes != null && classes.size() != 0)
		{
			aPC.setSkillPoints(0);
			for ( PCClass pcClass : classes )
			{
				pcClass.setSkillPool(0);
				if (aPC.getLevelInfoSize() > 0 && pcClass.getSafe(ObjectKey.MOD_TO_SKILLS))
				{
					final List<PCLevelInfo> pclList = aPC.getLevelInfo();
					for (int j = 0; j < pclList.size(); j++)
					{
						final PCLevelInfo pcl = pclList.get(j);
						if (pcl.getClassKeyName().equals(pcClass.getKeyName()))
						{
							final int spMod = pcClass.recalcSkillPointMod(aPC,
								j + 1);

							pcl.setSkillPointsGained(spMod);
							pcl.setSkillPointsRemaining(pcl
									.getSkillPointsGained());
							pcClass.setSkillPool(pcClass.skillPool() + spMod);

							aPC.setSkillPoints(spMod + aPC.getSkillPoints());
						}
					}
				}
			}
		}
	}
}

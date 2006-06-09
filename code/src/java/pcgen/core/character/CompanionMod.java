/*
 * CompanionMod.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 *************************************************************************
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @Created on July 10th, 2002, 3:55 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 *************************************************************************/
package pcgen.core.character;

import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.core.utils.IntegerKey;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.core.utils.StringKey;

import java.util.HashMap;
import java.util.Map;

/**
 * <code>CompanionMod</code>.
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision$
 */
public final class CompanionMod extends PObject
{
	private Map<String, String> classMap = new HashMap<String, String>();
	private Map<String, String> switchRaceMap = new HashMap<String, String>();
	private Map<String, String> varMap = new HashMap<String, String>();
	private boolean useMasterSkill;
	private String raceType = "";

	/**
	 * Bog standard clone method
	 *
	 * @return  a copy of this Ability
	 */
	public Object clone()
	{
		CompanionMod cmpMod = null;

		try
		{
			cmpMod = (CompanionMod) super.clone();
			cmpMod.classMap = classMap;
			cmpMod.switchRaceMap = switchRaceMap;
			cmpMod.varMap = varMap;
			cmpMod.useMasterSkill = useMasterSkill;
			cmpMod.raceType = raceType;
		}
		catch (CloneNotSupportedException e)
		{
			ShowMessageDelegate.showMessageDialog(
				e.getMessage(),
				Constants.s_APPNAME,
				MessageType.ERROR);
		}

		return cmpMod;
	}

	/**
	 * Compares classMap, level.
	 * @param obj the CompanionMod to compare with
	 * @return a negative integer, zero, or a positive integer as this object
	 *         is less than, equal to, or greater than the specified object.
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(final Object obj)
	{
		int result = 0;

		if (obj instanceof CompanionMod)
		{
			final CompanionMod cmpMod = (CompanionMod) obj;

			if (classMap.entrySet().equals(cmpMod.classMap.entrySet())
				&& getLevel() == cmpMod.getLevel())
			{
				result = 1;
			}
		}

		return result;
	}

	/**
	 * Compares classMap, type and level
	 * @param obj
	 * @return true if equal
	 */
	public boolean equals(final Object obj)
	{
		boolean result = false;

		if (obj instanceof CompanionMod)
		{
			final CompanionMod cmpMod = (CompanionMod) obj;

			if (classMap.entrySet().equals(cmpMod.classMap.entrySet())
				&& getLevel() == cmpMod.getLevel())
			{
				result = true;
			}
		}

		return result;
	}

	/**
	 * Get Class map
	 * @return classMap
	 */
	public Map<String, String> getClassMap()
	{
		return classMap;
	}

	/**
	 * Get a copy of the master Base Attack Bonus
	 * @return master Base Attack Bonus
	 */
	public String getCopyMasterBAB()
	{
		final String characteristic = stringChar.get(StringKey.MASTER_BAB_FORMULA);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Get a copy of the master check
	 * @return String
	 */
	public String getCopyMasterCheck()
	{
		final String characteristic = stringChar.get(StringKey.MASTER_CHECK_FORMULA);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Get a copy of the master HP
	 * @return master HP
	 */
	public String getCopyMasterHP()
	{
		final String characteristic = stringChar.get(StringKey.MASTER_HP_FORMULA);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Get Hit die
	 * @return Hit Die
	 */
	public int getHitDie()
	{
		final Integer characteristic = integerChar.get(IntegerKey.HIT_DIE);
		return characteristic == null ? 0 : characteristic.intValue();
	}

	/**
	 * Get Level
	 * @param className
	 * @return level
	 */
	public int getLevel(final String className)
	{
		int result = -1;

		if (classMap.get(className) != null)
		{
			result = Integer.parseInt(classMap.get(className));
		}
		else if (varMap.get(className) != null)
		{
			result = Integer.parseInt(varMap.get(className));
		}

		return result;
	}

	/**
	 * Get Race Type
	 * @return Race Type
	 */
	public String getRaceType()
	{
		return raceType;
	}

	/**
	 * Get switch race map
	 * @return switch race map
	 */
	public Map<String, String> getSwitchRaceMap()
	{
		return switchRaceMap;
	}

	/**
	 * Get use master skill
	 * @return true if you should use master skill
	 */
	public boolean getUseMasterSkill()
	{
		return useMasterSkill;
	}

	/**
	 * Get variable map
	 * @return varMap
	 */
	public Map<String, String> getVarMap()
	{
		return varMap;
	}

	/**
	 * Hashcode of the keyname
	 * @return hash code
	 */
	public int hashCode()
	{
		return classMap.hashCode();
	}

	/**
	 * Set the master BAB
	 * @param masterBABFormula
	 */
	public void setCopyMasterBAB(final String masterBABFormula)
	{
		stringChar.put(StringKey.MASTER_BAB_FORMULA, masterBABFormula);
	}

	/**
	 * Set the master formula
	 * @param masterCheckFormula
	 */
	public void setCopyMasterCheck(final String masterCheckFormula)
	{
		stringChar.put(StringKey.MASTER_CHECK_FORMULA, masterCheckFormula);
	}

	/**
	 * Set the master HP
	 * @param masterHPFormula
	 */
	public void setCopyMasterHP(final String masterHPFormula)
	{
		stringChar.put(StringKey.MASTER_HP_FORMULA, masterHPFormula);
	}

	/**
	 * Set the HD
	 * @param hd
	 */
	public void setHitDie(final int hd)
	{
		integerChar.put(IntegerKey.HIT_DIE, hd);
	}

	/**
	 * Set the level
	 * @param level
	 */
	public void setLevel(final int level)
	{
		integerChar.put(IntegerKey.LEVEL, level);
	}

	/**
	 * Set the Race Type
	 * @param aType
	 */
	public void setRaceType(final String aType)
	{
		raceType = aType;
	}

	/**
	 * Set the use master skill flag
	 * @param useMasterSkill
	 */
	public void setUseMasterSkill(final boolean useMasterSkill)
	{
		this.useMasterSkill = useMasterSkill;
	}

	private int getLevel()
	{
		final Integer characteristic = integerChar.get(IntegerKey.LEVEL);
		return characteristic == null ? 0 : characteristic.intValue();
	}
}

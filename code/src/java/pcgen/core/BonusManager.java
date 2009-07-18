/*
 * BonusManager
 * Copyright 2009 (c) Tom Parker <thpr@users.sourceforge.net>
 * derived from PlayerCharacter.java
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
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import pcgen.base.util.FixedStringList;
import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusObj.BonusPair;
import pcgen.core.bonus.util.MissingObject;
import pcgen.util.Delta;
import pcgen.util.Logging;

public class BonusManager
{
	private Map<String, String> activeBonusMap = new ConcurrentHashMap<String, String>();

	private List<BonusObj> activeBonusList = new ArrayList<BonusObj>();

	private List<BonusObj> tempBonusList = new ArrayList<BonusObj>();

	private Set<String> tempBonusFilters = new TreeSet<String>();

	private final PlayerCharacter pc;

	public BonusManager(PlayerCharacter p)
	{
		pc = p;
	}

	/**
	 * @param prefix
	 * @return Total bonus for prefix from the activeBonus HashMap
	 */
	private double sumActiveBonusMap(String prefix)
	{
		double bonus = 0;
		prefix = prefix.toUpperCase();

		final List<String> aList = new ArrayList<String>();

		// There is a risk that the active bonus map may be modified by other
		// threads, so we use a for loop rather than an iterator so that we
		// still get an answer.
		Object[] keys = activeBonusMap.keySet().toArray();
		for (int i = 0; i < keys.length; i++)
		{
			final String aKey = (String) keys[i];

			// aKey is either of the form:
			// COMBAT.AC
			// or
			// COMBAT.AC:Luck
			// or
			// COMBAT.AC:Armor.REPLACE
			if (aList.contains(aKey))
			{
				continue;
			}

			String rString = aKey;

			// rString could be something like:
			// COMBAT.AC:Armor.REPLACE
			// So need to remove the .STACK or .REPLACE
			// to get a match for prefix like: COMBAT.AC:Armor
			if (rString.endsWith(".STACK"))
			{
				rString = rString.substring(0, rString.length() - 6);
			}
			else if (rString.endsWith(".REPLACE"))
			{
				rString = rString.substring(0, rString.length() - 8);
			}

			// if prefix is of the form:
			// COMBAT.AC
			// then is must match rstring:
			// COMBAT.AC
			// COMBAT.AC:Luck
			// COMBAT.AC:Armor.REPLACE
			// However, it must not match
			// COMBAT.ACCHECK
			if ((rString.length() > prefix.length())
					&& !rString.startsWith(prefix + ":"))
			{
				continue;
			}

			if (rString.startsWith(prefix))
			{
				aList.add(rString);
				aList.add(rString + ".STACK");
				aList.add(rString + ".REPLACE");

				final double aBonus = getActiveBonusForMapKey(rString,
						Double.NaN);
				final double replaceBonus = getActiveBonusForMapKey(rString
						+ ".REPLACE", Double.NaN);
				final double stackBonus = getActiveBonusForMapKey(rString
						+ ".STACK", 0);
				//
				// Using NaNs in order to be able to get the max
				// between an undefined bonus and a negative
				//
				if (Double.isNaN(aBonus)) // no bonusKey
				{
					if (!Double.isNaN(replaceBonus))
					{
						// no bonusKey, but there
						// is a replaceKey
						bonus += replaceBonus;
					}
				}
				else if (Double.isNaN(replaceBonus))
				{
					// is a bonusKey and no replaceKey
					bonus += aBonus;
				}
				else
				{
					// is a bonusKey and a replaceKey
					bonus += Math.max(aBonus, replaceBonus);
				}

				// always add stackBonus
				bonus += stackBonus;
			}
		}

		return bonus;
	}

	/**
	 * Searches the activeBonus HashMap for aKey
	 * 
	 * @param aKey
	 * @param defaultValue
	 * 
	 * @return defaultValue if aKey not found
	 */
	private double getActiveBonusForMapKey(String aKey,
			final double defaultValue)
	{
		aKey = aKey.toUpperCase();

		final String regVal = activeBonusMap.get(aKey);

		if (regVal != null)
		{
			return Double.parseDouble(regVal);
		}

		return defaultValue;
	}

	public double getBonusDueToType(String mainType, String subType,
			String bonusType)
	{
		final String typeString = mainType + "." + subType + ":" + bonusType;

		return sumActiveBonusMap(typeString);
	}

	public double getTotalBonusTo(String bonusType, String bonusName)
	{
		final String prefix = new StringBuffer(bonusType).append('.').append(
				bonusName).toString();

		return sumActiveBonusMap(prefix);
	}

	public String getSpellBonusType(String bonusType, String bonusName)
	{
		String prefix = new StringBuffer(bonusType).append('.').append(
				bonusName).toString();
		prefix = prefix.toUpperCase();

		for (String aKey : activeBonusMap.keySet())
		{
			String aString = aKey;

			// rString could be something like:
			// COMBAT.AC:Armor.REPLACE
			// So need to remove the .STACK or .REPLACE
			// to get a match for prefix like: COMBAT.AC:Armor
			if (aKey.endsWith(".STACK"))
			{
				aString = aKey.substring(0, aKey.length() - 6);
			}
			else if (aKey.endsWith(".REPLACE"))
			{
				aString = aKey.substring(0, aKey.length() - 8);
			}

			// if prefix is of the form:
			// COMBAT.AC
			// then it must match
			// COMBAT.AC
			// COMBAT.AC:Luck
			// COMBAT.AC:Armor.REPLACE
			// However, it must not match
			// COMBAT.ACCHECK
			if ((aString.length() > prefix.length())
					&& !aString.startsWith(prefix + ":"))
			{
				continue;
			}

			if (aString.startsWith(prefix))
			{
				final int typeIndex = aString.indexOf(":");
				if (typeIndex > 0)
				{
					return (aKey.substring(typeIndex + 1)); // use aKey to get
					// .REPLACE or
					// .STACK
				}
				return Constants.EMPTY_STRING; // no type;
			}

		}

		return Constants.EMPTY_STRING; // just return no type
	}

	/**
	 * Build the bonus HashMap from all active BonusObj's
	 */
	void buildActiveBonusMap()
	{
		activeBonusMap.clear();
		Set<BonusObj> processedBonuses = new WrappedMapSet<BonusObj>(
				IdentityHashMap.class);

		//
		// We do a first pass of just the "static" bonuses
		// as they require less computation and no recursion
		List<BonusObj> bonusListCopy = new ArrayList<BonusObj>();
		bonusListCopy.addAll(getActiveBonusList());
		for (BonusObj bonus : bonusListCopy)
		{
			if (!bonus.isValueStatic())
			{
				continue;
			}

			final CDOMObject anObj = (CDOMObject) bonus.getCreatorObject();

			if (anObj == null)
			{
				Logging.debugPrint("BONUS: " + bonus
						+ " ignored due to no creator");
				continue;
			}

			// Keep track of which bonuses have been calculated
			processedBonuses.add(bonus);
			for (BonusPair bp : bonus.getStringListFromBonus(pc))
			{
				final double iBonus = bp.resolve(pc).doubleValue();
				setActiveBonusStack(iBonus, bp.bonusKey, activeBonusMap);
				Logging.debugPrint("BONUS: " + anObj.getDisplayName() + " : "
						+ iBonus + " : " + bp.bonusKey);
			}
		}

		//
		// Now we do all the BonusObj's that require calculations
		bonusListCopy = new ArrayList<BonusObj>();
		bonusListCopy.addAll(getActiveBonusList());
		for (BonusObj bonus : getActiveBonusList())
		{
			if (processedBonuses.contains(bonus))
			{
				continue;
			}

			final CDOMObject anObj = (CDOMObject) bonus.getCreatorObject();

			if (anObj == null)
			{
				continue;
			}

			processBonus(bonus, new WrappedMapSet<BonusObj>(
					IdentityHashMap.class), processedBonuses);
		}
	}

	public List<BonusObj> getActiveBonusList()
	{
		return activeBonusList;
	}

	public void setActiveBonusList(List<BonusObj> list)
	{
		activeBonusList = list;
	}

	public String listBonusesFor(String bonusType, String bonusName)
	{
		final String prefix = new StringBuffer(bonusType).append('.').append(
				bonusName).toString();
		final StringBuffer buf = new StringBuffer();
		final List<String> aList = new ArrayList<String>();

		// final List<TypedBonus> bonuses = theBonusMap.get(prefix);
		// if ( bonuses == null )
		// {
		// return Constants.EMPTY_STRING;
		// }
		// final List<String> bonusStrings =
		// TypedBonus.totalBonusesByType(bonuses);
		// return CoreUtility.commaDelimit(bonusStrings);

		final Set<String> keys = new TreeSet<String>();
		for (String aKey : activeBonusMap.keySet())
		{
			if (aKey.startsWith(prefix))
			{
				keys.add(aKey);
			}
		}
		for (String aKey : keys)
		{
			// make a list of keys that end with .REPLACE
			if (aKey.endsWith(".REPLACE"))
			{
				aList.add(aKey);
			}
			else
			{
				String reason = "";

				if (aKey.length() > prefix.length())
				{
					reason = aKey.substring(prefix.length() + 1);
				}

				final int b = (int) getActiveBonusForMapKey(aKey, 0);

				if (b == 0)
				{
					continue;
				}

				if (!"NULL".equals(reason) && (reason.length() > 0))
				{
					if (buf.length() > 0)
					{
						buf.append(", ");
					}
					buf.append(reason).append(' ');
				}
				buf.append(Delta.toString(b));
			}
		}

		// Now adjust the bonus if the .REPLACE value
		// replaces the value without .REPLACE
		for (String replaceKey : aList)
		{
			if (replaceKey.length() > 7)
			{
				final String aKey = replaceKey.substring(0,
						replaceKey.length() - 8);
				final double replaceBonus = getActiveBonusForMapKey(replaceKey,
						0);
				double aBonus = getActiveBonusForMapKey(aKey, 0);
				aBonus += getActiveBonusForMapKey(aKey + ".STACK", 0);

				final int b = (int) Math.max(aBonus, replaceBonus);

				if (b == 0)
				{
					continue;
				}

				if (buf.length() > 0)
				{
					buf.append(", ");
				}

				final String reason = aKey.substring(prefix.length() + 1);

				if (!"NULL".equals(reason))
				{
					buf.append(reason).append(' ');
				}

				buf.append(Delta.toString(b));
			}
		}

		return buf.toString();
	}

	/**
	 * - Get's a list of dependencies from aBonus - Finds all active bonuses
	 * that add to those dependencies and have not been processed and
	 * recursively calls itself - Once recursed in, it adds the computed bonus
	 * to activeBonusMap
	 * 
	 * @param aBonus
	 *            The bonus to be processed.
	 * @param prevProcessed
	 *            The list of bonuses which have already been processed in this
	 *            run.
	 * @param processedBonuses
	 *            TODO
	 */
	private void processBonus(final BonusObj aBonus,
			final Set<BonusObj> prevProcessed, Set<BonusObj> processedBonuses)
	{
		// Make sure we don't get into an infinite loop - can occur due to LST
		// coding or best guess dependancy mapping
		if (prevProcessed.contains(aBonus))
		{
			Logging
					.debugPrint("Ignoring bonus loop for " + aBonus + " as it was already processed. Bonuses already processed: " + prevProcessed); //$NON-NLS-1$//$NON-NLS-2$
			return;
		}
		prevProcessed.add(aBonus);

		final List<BonusObj> aList = new ArrayList<BonusObj>();

		// Go through all bonuses and check to see if they add to
		// aBonus's dependencies and have not already been processed
		for (BonusObj newBonus : getActiveBonusList())
		{
			if (processedBonuses.contains(newBonus))
			{
				continue;
			}

			if (aBonus.getDependsOn(newBonus.getUnparsedBonusInfoList()))
			{
				aList.add(newBonus);
			}
		}

		// go through all the BonusObj's that aBonus depends on
		// and process them first
		for (BonusObj newBonus : aList)
		{
			// Recursively call itself
			processBonus(newBonus, prevProcessed, processedBonuses);
		}

		// Double check that it hasn't been processed yet
		if (processedBonuses.contains(aBonus))
		{
			return;
		}

		// Add to processed list
		processedBonuses.add(aBonus);

		final CDOMObject anObj = (CDOMObject) aBonus.getCreatorObject();

		if (anObj == null)
		{
			prevProcessed.remove(aBonus);
			return;
		}

		// calculate bonus and add to activeBonusMap
		for (BonusPair bp : aBonus.getStringListFromBonus(pc))
		{
			final double iBonus = bp.resolve(pc).doubleValue();
			setActiveBonusStack(iBonus, bp.bonusKey, activeBonusMap);
			Logging.debugPrint("vBONUS: " + anObj.getDisplayName() + " : "
					+ iBonus + " : " + bp.bonusKey);
		}
		prevProcessed.remove(aBonus);
	}

	/**
	 * Figures out if a bonus should stack based on type, then adds it to the
	 * supplied map.
	 * 
	 * @param bonus
	 *            The value of the bonus.
	 * @param bonusType
	 *            The type of the bonus e.g. STAT.DEX:LUCK
	 * @param bonusMap
	 *            The bonus map being built up.
	 */
	private void setActiveBonusStack(double bonus, String bonusType,
			Map<String, String> bonusMap)
	{
		if (bonusType != null)
		{
			bonusType = bonusType.toUpperCase();

			// only specific bonuses can actually be fractional
			// -> TODO should define this in external file
			if (!bonusType.startsWith("ITEMWEIGHT")
					&& !bonusType.startsWith("ITEMCOST")
					&& !bonusType.startsWith("ACVALUE")
					&& !bonusType.startsWith("ITEMCAPACITY")
					&& !bonusType.startsWith("LOADMULT")
					&& !bonusType.startsWith("FEAT")
					&& (bonusType.indexOf("DAMAGEMULT") < 0))
			{
				bonus = ((int) bonus); // TODO: never used
			}
		}
		else
		{
			return;
		}

		// default to non-stacking bonuses
		int index = -1;

		// bonusType is either of form:
		// COMBAT.AC
		// or
		// COMBAT.AC:Luck
		// or
		// COMBAT.AC:Armor.REPLACE
		//
		final StringTokenizer aTok = new StringTokenizer(bonusType, ":");

		if (aTok.countTokens() == 2)
		{
			// need 2nd token to see if it should stack
			final String aString;
			aTok.nextToken();
			aString = aTok.nextToken();

			if (aString != null)
			{
				index = SettingsHandler.getGame()
						.getUnmodifiableBonusStackList().indexOf(aString); // e.g.
				// Dodge
			}
		}
		else
		{
			// un-named (or un-TYPE'd) bonuses stack
			index = 1;
		}

		// .STACK means stack with everything
		// .REPLACE means stack with other .REPLACE
		if (bonusType.endsWith(".STACK") || bonusType.endsWith(".REPLACE"))
		{
			index = 1;
		}

		// If it's a negative bonus, it always needs to be added
		if (bonus < 0)
		{
			index = 1;
		}

		if (index == -1) // a non-stacking bonus
		{
			final String aVal = bonusMap.get(bonusType);

			if (aVal == null)
			{
				putActiveBonusMap(bonusType, String.valueOf(bonus), bonusMap);
			}
			else
			{
				putActiveBonusMap(bonusType, String.valueOf(Math.max(bonus,
						Float.parseFloat(aVal))), bonusMap);
			}
		}
		else
		// a stacking bonus
		{
			final String aVal = bonusMap.get(bonusType);

			if (aVal == null)
			{
				putActiveBonusMap(bonusType, String.valueOf(bonus), bonusMap);
			}
			else
			{
				putActiveBonusMap(bonusType, String.valueOf(bonus
						+ Float.parseFloat(aVal)), bonusMap);
			}
		}
	}

	/**
	 * Put the provided bonus key and value into the supplied bonus map. Some
	 * sanity checking is done on the key.
	 * 
	 * @param aKey
	 *            The bonus key
	 * @param aVal
	 *            The value of the bonus
	 * @param bonusMap
	 *            The map of bonuses being built.
	 */
	private void putActiveBonusMap(final String aKey, final String aVal,
			Map<String, String> bonusMap)
	{
		//
		// This is a bad idea...will add whatever the bonus is to ALL skills
		//
		if (aKey.equalsIgnoreCase("SKILL.LIST"))
		{
			pc.setDisplayUpdate(true);
			return;
		}
		bonusMap.put(aKey, aVal);
		// setDirty(true);
	}

	public int getPartialStatBonusFor(PCStat stat, boolean useTemp,
			boolean useEquip)
	{
		// List<BonusObj> abl = getAllActiveBonuses();
		List<BonusObj> abl = getActiveBonusList();
		String statAbbr = stat.getAbb();
		final String prefix = "STAT." + statAbbr;
		Map<String, String> bonusMap = new HashMap<String, String>();

		for (BonusObj bonus : abl)
		{
			if (isApplied(bonus) && bonus.getBonusName().equals("STAT"))
			{
				boolean found = false;
				for (Object element : bonus.getBonusInfoList())
				{
					if (element instanceof PCStat
							&& ((PCStat) element).equals(stat))
					{
						found = true;
						break;
					}
					// TODO: This should be put into a proper object when
					// parisng.
					if (element instanceof MissingObject)
					{
						String name = ((MissingObject) element).getObjectName();
						if (("%LIST".equals(name) || "LIST".equals(name))
								&& bonus.getCreatorObject() instanceof CDOMObject)
						{
							CDOMObject creator = (CDOMObject) bonus
									.getCreatorObject();
							for (FixedStringList assoc : pc
									.getDetailedAssociations(creator))
							{
								if (assoc.contains(statAbbr))
								{
									found = true;
									break;
								}
							}
						}
					}
				}
				if (!found)
				{
					continue;
				}

				// The bonus has been applied to the target stat
				// Should it be included?
				boolean addIt = false;
				if (bonus.getCreatorObject() instanceof Equipment
						|| bonus.getCreatorObject() instanceof EquipmentModifier)
				{
					addIt = useEquip;
				}
				else if (tempBonusList.contains(bonus))
				{
					addIt = useTemp;
				}
				else
				{
					addIt = true;
				}
				if (addIt)
				{
					// Grab the list of relevant types so that we can build up
					// the
					// bonuses with the stacking rules applied.
					for (BonusPair bp : bonus.getStringListFromBonus(pc))
					{
						if (bp.bonusKey.startsWith(prefix))
						{
							setActiveBonusStack(bp.resolve(pc).doubleValue(),
									bp.bonusKey, bonusMap);
						}
					}
				}
			}
		}
		// Sum the included bonuses to the stat to get our result.
		int total = 0;
		for (String bKey : bonusMap.keySet())
		{
			total += Float.parseFloat(bonusMap.get(bKey));
		}
		return total;
	}

	public List<BonusObj> getTempBonusList()
	{
		return tempBonusList;
	}

	public BonusManager buildDeepClone(PlayerCharacter apc)
	{
		BonusManager clone = new BonusManager(apc);
		clone.activeBonusList.addAll(activeBonusList);
		clone.tempBonusList.addAll(tempBonusList);
		clone.activeBonusMap.putAll(activeBonusMap);
		clone.tempBonusFilters.addAll(tempBonusFilters);
		return clone;
	}

	public String getBonusMapString()
	{
		return activeBonusMap.toString();
	}

	public void setTempBonusList(List<BonusObj> tbl)
	{
		tempBonusList = tbl;
	}

	public Map<String, String> getBonuses(String bonusString, String substring)
	{
		Map<String, String> returnMap = new HashMap<String, String>();
		String prefix = bonusString + "." + substring + ".";

		for (Map.Entry<String, String> entry : activeBonusMap.entrySet())
		{
			String aKey = entry.getKey();

			if (aKey.startsWith(prefix))
			{
				returnMap.put(aKey, entry.getValue());
			}
		}
		return returnMap;
	}

	public void addTempBonus(BonusObj bonus)
	{
		tempBonusList.add(bonus);
	}

	public void removeTempBonus(BonusObj bonus)
	{
		tempBonusList.remove(bonus);
	}

	public Set<String> getTempBonusNames()
	{
		final Set<String> ret = new TreeSet<String>();
		for (BonusObj bonus : tempBonusList)
		{
			ret.add(bonus.getName());
		}
		return ret;
	}

	public List<BonusObj> getTempBonusList(String aCreator, String aTarget)
	{
		final List<BonusObj> aList = new ArrayList<BonusObj>();

		for (BonusObj bonus : tempBonusList)
		{
			final Object aTO = bonus.getTargetObject();
			final Object aCO = bonus.getCreatorObject();

			String targetName = Constants.EMPTY_STRING;
			String creatorName = Constants.EMPTY_STRING;

			if (aCO instanceof CDOMObject)
			{
				creatorName = ((CDOMObject) aCO).getKeyName();
			}

			if (aTO instanceof PlayerCharacter)
			{
				targetName = ((PlayerCharacter) aTO).getName();
			}
			else if (aTO instanceof PObject)
			{
				targetName = ((PObject) aTO).getKeyName();
			}

			if (creatorName.equals(aCreator) && targetName.equals(aTarget))
			{
				aList.add(bonus);
			}
		}

		return aList;
	}

	public List<String> getNamedTempBonusList()
	{
		final List<String> aList = new ArrayList<String>();

		for (BonusObj aBonus : tempBonusList)
		{
			if (aBonus == null)
			{
				continue;
			}

			if (!isApplied(aBonus))
			{
				continue;
			}

			final CDOMObject aCreator = (CDOMObject) aBonus.getCreatorObject();

			if (aCreator == null)
			{
				continue;
			}

			final String aName = aCreator.getKeyName();

			if (!aList.contains(aName))
			{
				aList.add(aName);
			}
		}

		return aList;
	}

	public List<String> getNamedTempBonusDescList()
	{
		final List<String> aList = new ArrayList<String>();

		for (BonusObj aBonus : tempBonusList)
		{
			if (aBonus == null)
			{
				continue;
			}

			if (!isApplied(aBonus))
			{
				continue;
			}

			final CDOMObject aCreator = (CDOMObject) aBonus.getCreatorObject();

			if (aCreator == null)
			{
				continue;
			}

			String aDesc = aCreator.getSafe(StringKey.DESCRIPTION);

			if (!aList.contains(aDesc))
			{
				aList.add(aDesc);
			}
		}

		return aList;
	}

	public List<BonusObj> getFilteredTempBonusList()
	{
		final List<BonusObj> ret = new ArrayList<BonusObj>();
		for (BonusObj bonus : tempBonusList)
		{
			if (!tempBonusFilters.contains(bonus.getName()))
			{
				ret.add(bonus);
			}
		}
		return ret;
	}

	public Set<String> getTempBonusFilters()
	{
		return tempBonusFilters;
	}

	public void addTempBonusFilter(String bonusStr)
	{
		tempBonusFilters.add(bonusStr);
	}

	public void removeTempBonusFilter(String bonusStr)
	{
		tempBonusFilters.remove(bonusStr);
	}

	public List<BonusObj> getTempBonuses()
	{
		final List<BonusObj> tempList = getFilteredTempBonusList();
		if (tempList.isEmpty())
		{
			return Collections.emptyList();
		}
		for (final Iterator<BonusObj> tempIter = tempList.iterator(); tempIter
				.hasNext();)
		{
			final BonusObj bonus = tempIter.next();
			bonus.setApplied(pc, false);

			if (bonus.qualifies(pc))
			{
				bonus.setApplied(pc, true);
			}

			if (!isApplied(bonus))
			{
				tempIter.remove();
			}
		}
		return tempList;
	}

	public boolean isApplied(BonusObj bonus)
	{
		return bonus.isApplied(pc);
	}

}

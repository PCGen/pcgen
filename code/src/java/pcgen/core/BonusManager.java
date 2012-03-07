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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import pcgen.base.formula.Formula;
import pcgen.base.util.FixedStringList;
import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.BonusContainer;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusPair;
import pcgen.core.bonus.util.MissingObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.util.Delta;
import pcgen.util.Logging;

public class BonusManager
{
	/** %LIST - Replace one value selected into this spot */
	private static final String VALUE_TOKEN_REPLACEMENT = "%LIST"; //$NON-NLS-1$
	/** LIST - Replace all the values selected into this spot */
	private static final String LIST_TOKEN_REPLACEMENT = "LIST"; //$NON-NLS-1$

	private static final String VALUE_TOKEN_PATTERN = Pattern
			.quote(VALUE_TOKEN_REPLACEMENT);

	private static final String VAR_TOKEN_REPLACEMENT = "%VAR"; //$NON-NLS-1$

	private static final String VAR_TOKEN_PATTERN = Pattern
			.quote(VAR_TOKEN_REPLACEMENT);

	private static final FixedStringList NO_ASSOC = new FixedStringList("");

	private static final List<FixedStringList> NO_ASSOC_LIST = Collections
			.singletonList(NO_ASSOC);

	private Map<String, String> activeBonusMap = new ConcurrentHashMap<String, String>();

	private Map<BonusObj, Object> activeBonusBySource = new IdentityHashMap<BonusObj, Object>();

	private Map<BonusObj, TempBonusInfo> tempBonusBySource = new IdentityHashMap<BonusObj, TempBonusInfo>();

	private Set<String> tempBonusFilters = new TreeSet<String>();

	private final PlayerCharacter pc;
	private Map<String, String> checkpointMap;

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

		for (String aKey : activeBonusMap.keySet())
		{
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
		activeBonusMap = new ConcurrentHashMap<String, String>();
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

			final Object source = getSourceObject(bonus);

			if (source == null)
			{
				Logging.debugPrint("BONUS: " + bonus
						+ " ignored due to no creator");
				continue;
			}

			// Keep track of which bonuses have been calculated
			processedBonuses.add(bonus);
			for (BonusPair bp : getStringListFromBonus(bonus))
			{
				final double iBonus = bp.resolve(pc).doubleValue();
				setActiveBonusStack(iBonus, bp.bonusKey, activeBonusMap);
				if (Logging.isDebugMode())
				{
					String id;
					if (source instanceof CDOMObject)
					{
						id = ((CDOMObject)source).getDisplayName();
					}
					else
					{
						id = source.toString();
					}
					Logging.debugPrint("BONUS: " + id + " : " + iBonus + " : "
							+ bp.bonusKey);
				}
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

			final CDOMObject anObj = (CDOMObject) getSourceObject(bonus);

			if (anObj == null)
			{
				continue;
			}

			processBonus(bonus, new WrappedMapSet<BonusObj>(
					IdentityHashMap.class), processedBonuses);
		}
	}

	public Collection<BonusObj> getActiveBonusList()
	{
		return activeBonusBySource.keySet();
	}

	public void setActiveBonusList()
	{
		activeBonusBySource = getAllActiveBonuses();
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

		final CDOMObject anObj = (CDOMObject) getSourceObject(aBonus);

		if (anObj == null)
		{
			prevProcessed.remove(aBonus);
			return;
		}

		// calculate bonus and add to activeBonusMap
		for (BonusPair bp : getStringListFromBonus(aBonus))
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
	}

	public int getPartialStatBonusFor(PCStat stat, boolean useTemp,
			boolean useEquip)
	{
		String statAbbr = stat.getAbb();
		final String prefix = "STAT." + statAbbr;
		Map<String, String> bonusMap = new HashMap<String, String>();

		for (BonusObj bonus : getActiveBonusList())
		{
			if (pc.isApplied(bonus) && bonus.getBonusName().equals("STAT"))
			{
				boolean found = false;
				Object co = getSourceObject(bonus);
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
								&& co instanceof CDOMObject)
						{
							CDOMObject creator = (CDOMObject) co;
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
				if (co instanceof Equipment || co instanceof EquipmentModifier)
				{
					addIt = useEquip;
				}
				else if (tempBonusBySource.containsKey(bonus))
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
					for (BonusPair bp : getStringListFromBonus(bonus))
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

	public BonusManager buildDeepClone(PlayerCharacter apc)
	{
		BonusManager clone = new BonusManager(apc);
		clone.activeBonusBySource.putAll(activeBonusBySource);
		clone.tempBonusBySource.putAll(tempBonusBySource);
		clone.activeBonusMap.putAll(activeBonusMap);
		clone.tempBonusFilters.addAll(tempBonusFilters);
		return clone;
	}

	public void checkpointBonusMap()
	{
		checkpointMap = activeBonusMap;
	}

	public boolean compareToCheckpoint()
	{
		return checkpointMap != null && checkpointMap.equals(activeBonusMap);
	}

	public void setTempBonusMap(Map<BonusObj, TempBonusInfo> tbl)
	{
		//Need to guarantee identity
		tempBonusBySource = new IdentityHashMap<BonusObj, TempBonusInfo>(tbl);
	}

	public Map<BonusObj, TempBonusInfo> getTempBonusMap()
	{
		return new IdentityHashMap<BonusObj, TempBonusInfo>(tempBonusBySource);
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

	public void addTempBonus(BonusObj bonus, Object source, Object target)
	{
		tempBonusBySource.put(bonus, new TempBonusInfo(source, target));
	}

	public void removeTempBonus(BonusObj bonus)
	{
		tempBonusBySource.remove(bonus);
	}

	public Set<String> getTempBonusNames()
	{
		final Set<String> ret = new TreeSet<String>();
		for (Map.Entry<BonusObj, TempBonusInfo> me : tempBonusBySource
				.entrySet())
		{
			ret.add(getBonusName(me.getKey(), me.getValue()));
		}
		return ret;
	}

	public List<BonusObj> getTempBonusList(String aCreator, String aTarget)
	{
		final List<BonusObj> aList = new ArrayList<BonusObj>();

		for (Map.Entry<BonusObj, TempBonusInfo> me : tempBonusBySource
				.entrySet())
		{
			BonusObj bonus = me.getKey();
			final Object aTO = me.getValue().target;
			final Object aCO = me.getValue().source;

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
			else if (aTO instanceof CDOMObject)
			{
				targetName = ((CDOMObject) aTO).getKeyName();
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

		for (Map.Entry<BonusObj, TempBonusInfo> me : tempBonusBySource
				.entrySet())
		{
			BonusObj aBonus = me.getKey();
			if (aBonus == null)
			{
				continue;
			}

			if (!pc.isApplied(aBonus))
			{
				continue;
			}

			final CDOMObject aCreator = (CDOMObject) me.getValue().source;

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

		for (Map.Entry<BonusObj, TempBonusInfo> me : tempBonusBySource
				.entrySet())
		{
			BonusObj aBonus = me.getKey();
			if (aBonus == null)
			{
				continue;
			}

			if (!pc.isApplied(aBonus))
			{
				continue;
			}

			final CDOMObject aCreator = (CDOMObject) me.getValue().source;

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

	public Map<BonusObj, TempBonusInfo> getFilteredTempBonusList()
	{
		final Map<BonusObj, TempBonusInfo> ret = new IdentityHashMap<BonusObj, TempBonusInfo>();
		for (Map.Entry<BonusObj, TempBonusInfo> me : tempBonusBySource
				.entrySet())
		{
			BonusObj bonus = me.getKey();
			TempBonusInfo ti = me.getValue();
			if (!tempBonusFilters.contains(getBonusName(bonus, ti)))
			{
				ret.put(bonus, ti);
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

	public Map<BonusObj, Object> getTempBonuses()
	{
		Map<BonusObj, Object> map = new IdentityHashMap<BonusObj, Object>();
		for (Map.Entry<BonusObj, TempBonusInfo> me : getFilteredTempBonusList()
				.entrySet())
		{
			final BonusObj bonus = me.getKey();
			pc.setApplied(bonus, false);

			Object source = me.getValue().source;
			CDOMObject cdomsource = (source instanceof CDOMObject) ? (CDOMObject) source
					: null;
			if (bonus.qualifies(pc, cdomsource))
			{
				pc.setApplied(bonus, true);
			}

			if (pc.isApplied(bonus))
			{
				map.put(bonus, source);
			}
		}
		return map;
	}

	public Map<BonusObj, TempBonusInfo> getTempBonusMap(String aCreator,
			String aTarget)
	{
		final Map<BonusObj, TempBonusInfo> aMap = new IdentityHashMap<BonusObj, TempBonusInfo>();

		for (Map.Entry<BonusObj, TempBonusInfo> me : tempBonusBySource
				.entrySet())
		{
			BonusObj bonus = me.getKey();
			TempBonusInfo tbi = me.getValue();
			final Object aTO = tbi.target;
			final Object aCO = tbi.source;

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
			else if (aTO instanceof CDOMObject)
			{
				targetName = ((CDOMObject) aTO).getKeyName();
			}

			if (creatorName.equals(aCreator) && targetName.equals(aTarget))
			{
				aMap.put(bonus, tbi);
			}
		}

		return aMap;
	}

	public String getBonusContext(BonusObj bo, boolean shortForm)
	{
		final StringBuilder sb = new StringBuilder(50);

		boolean bEmpty = true;
		sb.append('[');
		if (bo.hasPrerequisites())
		{
			for (Prerequisite p : bo.getPrerequisiteList())
			{
				if (!bEmpty)
				{
					sb.append('|');
				}
				sb.append(p.getDescription(shortForm));
				bEmpty = false;
			}
		}

		String type = bo.getTypeString();
		if (type.length() != 0)
		{
			if (!shortForm)
			{
				if (!bEmpty)
				{
					sb.append('|');
				}
				sb.append("TYPE=");
				bEmpty = false;
			}
			if (!shortForm || sb.charAt(sb.length() - 1) == '[')
			{
				sb.append(type);
				bEmpty = false;
			}
		}

		//
		// If there is nothing shown in between the [], then show the Bonus's
		// type
		//
		if (bEmpty)
		{
			sb.append(getSourceString(bo));
		}
		sb.append(']');

		return sb.toString();
	}

	private String getSourceString(BonusObj bo)
	{
		Object source = getSourceObject(bo);
		if (source == null)
		{
			return "NONE";
		}
		if (source instanceof PlayerCharacter)
		{
			return ((PlayerCharacter) source).getName();
		}
		else
		// if (source instanceof PObject)
		{
			return source.toString();
		}
	}

	private Object getSourceObject(BonusObj bo)
	{
		Object source = activeBonusBySource.get(bo);
		if (source == null)
		{
			TempBonusInfo tbi = tempBonusBySource.get(bo);
			if (tbi != null)
			{
				source = tbi.source;
			}
		}
		return source;
	}

	/**
	 * Returns a String which can be used to display in the GUI
	 * 
	 * @return name
	 */
	public String getBonusName(BonusObj bonus, TempBonusInfo ti)
	{
		final StringBuilder buffer = new StringBuilder();

		buffer.append(ti.source.toString());
		buffer.append(" [");

		Object targetObj = ti.target;

		if (targetObj instanceof PlayerCharacter)
		{
			buffer.append("PC");
		}
		else if (targetObj instanceof Equipment)
		{
			buffer.append(((Equipment) targetObj).getName());
		}
		else
		{
			buffer.append("NONE");
		}

		buffer.append(']');

		return buffer.toString();
	}

	public List<BonusPair> getStringListFromBonus(BonusObj bo)
	{
		Object creatorObj = getSourceObject(bo);

		List<FixedStringList> associatedList;
		CDOMObject anObj = null;
		if (creatorObj instanceof CDOMObject)
		{
			anObj = (CDOMObject) creatorObj;
			associatedList = pc.getDetailedAssociations(anObj);
			if (associatedList == null || associatedList.isEmpty())
			{
				associatedList = NO_ASSOC_LIST;
			}
		}
		else
		{
			associatedList = NO_ASSOC_LIST;
		}

		List<BonusPair> bonusList = new ArrayList<BonusPair>();

		// Must use getBonusName because it contains the unaltered bonusType
		String name = bo.getBonusName();
		String[] infoArray = bo.getBonusInfo().split(",");
		String thisType = bo.getTypeString();

		for (FixedStringList assoc : associatedList)
		{
			StringBuilder asb = new StringBuilder();
			int size = assoc.size();
			if (size == 1)
			{
				asb.append(assoc.get(0));
			}
			else
			{
				asb.append(size).append(':');
				int loc = asb.length();
				int count = 0;
				for (String s : assoc)
				{
					if (s != null)
					{
						count++;
						asb.append(':').append(s);
					}
				}
				asb.insert(loc, count);
			}
			String assocString = asb.toString();

			String thisName;
			if (name.indexOf(VALUE_TOKEN_REPLACEMENT) >= 0)
			{
				thisName = name.replaceAll(VALUE_TOKEN_PATTERN, assocString);
			}
			else
			{
				thisName = name;
			}
			List<String> infoList = new ArrayList<String>(4);
			for (String info : infoArray)
			{
				if (info.indexOf(VALUE_TOKEN_REPLACEMENT) >= 0)
				{
					for (String expInfo : assoc)
					{
						infoList.add(info.replaceAll(VALUE_TOKEN_PATTERN,
								expInfo));
					}
				}
				else if (info.indexOf(VAR_TOKEN_REPLACEMENT) >= 0)
				{
					infoList.add(name
							.replaceAll(VAR_TOKEN_PATTERN, assocString));
				}
				else if (info.equals(LIST_TOKEN_REPLACEMENT))
				{
					infoList.add(assocString);
				}
				else
				{
					infoList.add(info);
				}
			}
			Formula newFormula;
			if (bo.isValueStatic())
			{
				newFormula = bo.getFormula();
			}
			else
			{
				String value = bo.getValue();

				// A %LIST substitution also needs to be done in the val
				// section
				int listIndex = value.indexOf(VALUE_TOKEN_REPLACEMENT);
				String thisValue = value;
				if (listIndex >= 0)
				{
					thisValue = value.replaceAll(VALUE_TOKEN_PATTERN,
							assocString);
				}
				//Need to protect against a selection not being made with a %LIST
				if (thisValue.length() == 0)
				{
					thisValue = "0";
				}
				newFormula = FormulaFactory.getFormulaFor(thisValue);
			}
			for (String thisInfo : infoList)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(thisName).append('.').append(thisInfo);
				if (bo.hasTypeString())
				{
					sb.append(':').append(thisType);
				}
				bonusList.add(new BonusPair(sb.toString(), newFormula,
						creatorObj));
			}
		}
		return bonusList;
	}

	public static class TempBonusInfo
	{
		public final Object source;
		public final Object target;

		public TempBonusInfo(Object src, Object tgt)
		{
			source = src;
			target = tgt;
		}
	}

	public double calcBonusesWithCost(List<BonusObj> list)
	{
		double totalBonus = 0;

		for (BonusObj aBonus : list)
		{
			final CDOMObject anObj = (CDOMObject) getSourceObject(aBonus);

			if (anObj == null)
			{
				continue;
			}

			double iBonus = 0;

			if (aBonus.qualifies(pc, anObj))
			{
				iBonus = aBonus.resolve(pc, anObj.getQualifiedKey())
						.doubleValue();
			}

			int k;
			if (pc.hasAssociations(anObj))
			{
				k = 0;

				for (String aString : pc.getAssociationList(anObj))
				{
					if (aString.equalsIgnoreCase(aBonus.getBonusInfo()))
					{
						++k;
					}
				}
			}
			else
			{
				k = 1;
			}

			if ((k == 0) && !CoreUtility.doublesEqual(iBonus, 0))
			{
				totalBonus += iBonus;
			}
			else
			{
				totalBonus += (iBonus * k);
			}
		}

		return totalBonus;
	}

	public boolean hasTempBonusesApplied(CDOMObject mod)
	{
		for (TempBonusInfo tbi : tempBonusBySource.values())
		{
			if (tbi.source.equals(mod))
			{
				return true;
			}
		}
		return false;
	}

	private Map<BonusObj, Object> getAllActiveBonuses()
	{
		Map<BonusObj, Object> ret = new IdentityHashMap<BonusObj, Object>();

		for (final BonusContainer pobj : pc.getBonusContainerList())
		{
			// We exclude equipmods here as their bonuses are already counted in
			// the equipment they belong to.
			if (pobj != null && !(pobj instanceof EquipmentModifier))
			{
				boolean use = true;
				if (pobj instanceof PCClass)
				{
					// Class bonuses are only included if the level is greater
					// than 0
					// This is because 0 levels of a class can be added to
					// access spell casting etc
					use = pc.getLevel(((PCClass) pobj)) > 0;
				}
				if (use)
				{
					pobj.activateBonuses(pc);
					List<BonusObj> abs = pobj.getActiveBonuses(pc);
					for (BonusObj bo : abs)
					{
						ret.put(bo, pobj);
					}
				}
			}
		}

		if (pc.getUseTempMods())
		{
			ret.putAll(getTempBonuses());
		}

		return ret;
	}
}

/*
 * BonusManager
 * Copyright 2009 (c) Tom Parker <thpr@users.sourceforge.net>
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
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.BonusContainer;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.analysis.ChooseActivation;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusPair;
import pcgen.core.bonus.util.MissingObject;
import pcgen.core.display.BonusDisplay;
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

	private static final String VALUE_TOKEN_PATTERN = Pattern.quote(VALUE_TOKEN_REPLACEMENT);

	private static final String VAR_TOKEN_REPLACEMENT = "%VAR"; //$NON-NLS-1$

	private static final String VAR_TOKEN_PATTERN = Pattern.quote(VAR_TOKEN_REPLACEMENT);

	private static final List<String> NO_ASSOC_LIST = Collections.singletonList("");

	private Map<String, String> activeBonusMap = new ConcurrentHashMap<>();

	private Map<String, Double> cachedActiveBonusSumsMap = new ConcurrentHashMap<>();

	private Map<BonusObj, Object> activeBonusBySource = new IdentityHashMap<>();

	private final Map<BonusObj, TempBonusInfo> tempBonusBySource = new IdentityHashMap<>();

	private final Set<String> tempBonusFilters = new TreeSet<>();

	private final PlayerCharacter pc;
	private Map<String, String> checkpointMap;

	public BonusManager(PlayerCharacter p)
	{
		pc = p;
	}

	/**
	 * @param fullyQualifiedBonusType
	 * @return Total bonus for prefix from the activeBonus HashMap
	 */
	private double sumActiveBonusMap(String fullyQualifiedBonusType)
	{
		double bonus = 0;
		if (fullyQualifiedBonusType == null)
		{
			Logging.errorPrint("Unable to sum BONUS when request is null");
			return bonus;
		}

		fullyQualifiedBonusType = fullyQualifiedBonusType.toUpperCase();
		if (cachedActiveBonusSumsMap.containsKey(fullyQualifiedBonusType))
		{
			return cachedActiveBonusSumsMap.get(fullyQualifiedBonusType);
		}

		final List<String> aList = new ArrayList<>();
		boolean found = false;

		for (String fullyQualifedCurrentBonus : activeBonusMap.keySet())
		{
			// aKey is either of the form:
			// COMBAT.AC
			// or
			// COMBAT.AC:Luck
			// or
			// COMBAT.AC:Armor.REPLACE
			if (aList.contains(fullyQualifedCurrentBonus))
			{
				continue;
			}

			if (fullyQualifedCurrentBonus == null)
			{
				Logging.errorPrint(
					"null BONUS: loaded into activeBonusMap. " + fullyQualifiedBonusType + " was requested");
				continue;
			}

			String currentTypedBonusNameInfo = fullyQualifedCurrentBonus;

			// rString could be something like:
			// COMBAT.AC:Armor.REPLACE
			// So need to remove the .STACK or .REPLACE
			// to get a match for prefix like: COMBAT.AC:Armor
			if (currentTypedBonusNameInfo.endsWith(".STACK"))
			{
				currentTypedBonusNameInfo =
						currentTypedBonusNameInfo.substring(0, currentTypedBonusNameInfo.length() - 6);
			}
			else if (currentTypedBonusNameInfo.endsWith(".REPLACE"))
			{
				currentTypedBonusNameInfo =
						currentTypedBonusNameInfo.substring(0, currentTypedBonusNameInfo.length() - 8);
			}

            // if prefix is of the form:
			// COMBAT.AC
			// then is must match rstring:
			// COMBAT.AC
			// COMBAT.AC:Luck
			// COMBAT.AC:Armor.REPLACE
			// However, it must not match
			// COMBAT.ACCHECK
			if ((currentTypedBonusNameInfo.length() > fullyQualifiedBonusType.length())
				&& !currentTypedBonusNameInfo.startsWith(fullyQualifiedBonusType + ":"))
			{
				continue;
			}

			if (currentTypedBonusNameInfo.startsWith(fullyQualifiedBonusType))
			{
				found = true;
				aList.add(currentTypedBonusNameInfo);
				aList.add(currentTypedBonusNameInfo + ".STACK");
				aList.add(currentTypedBonusNameInfo + ".REPLACE");

				final double aBonus = getActiveBonusForMapKey(currentTypedBonusNameInfo, Double.NaN);
				final double replaceBonus = getActiveBonusForMapKey(currentTypedBonusNameInfo + ".REPLACE", Double.NaN);
				final double stackBonus = getActiveBonusForMapKey(currentTypedBonusNameInfo + ".STACK", 0);
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

		// cache value only if it has been positively found
		if (found)
		{
			cachedActiveBonusSumsMap.put(fullyQualifiedBonusType, bonus);
		}
		return bonus;
	}

	/**
	 * Searches the activeBonus HashMap for aKey
	 * 
	 * @param fullyQualifiedBonusType
	 * @param defaultValue
	 * 
	 * @return defaultValue if aKey not found
	 */
	private double getActiveBonusForMapKey(String fullyQualifiedBonusType, final double defaultValue)
	{
		fullyQualifiedBonusType = fullyQualifiedBonusType.toUpperCase();

		final String regVal = activeBonusMap.get(fullyQualifiedBonusType);

		if (regVal != null)
		{
			return Double.parseDouble(regVal);
		}

		return defaultValue;
	}

	public double getBonusDueToType(String bonusName, String bonusInfo, String bonusType)
	{
		final String typeString = bonusName + "." + bonusInfo + ":" + bonusType;

		return sumActiveBonusMap(typeString);
	}

	public double getTotalBonusTo(String bonusName, String bonusInfo)
	{
		final String prefix = bonusName + '.' + bonusInfo;

		return sumActiveBonusMap(prefix);
	}

	public String getSpellBonusType(String bonusName, String bonusInfo)
	{
		String prefix = bonusName + '.' + bonusInfo;
		prefix = prefix.toUpperCase();

		for (String fullyQualifedBonusType : activeBonusMap.keySet())
		{
			String typedBonusNameInfo = fullyQualifedBonusType;

			// rString could be something like:
			// COMBAT.AC:Armor.REPLACE
			// So need to remove the .STACK or .REPLACE
			// to get a match for prefix like: COMBAT.AC:Armor
			if (fullyQualifedBonusType.endsWith(".STACK"))
			{
				typedBonusNameInfo = fullyQualifedBonusType.substring(0, fullyQualifedBonusType.length() - 6);
			}
			else if (fullyQualifedBonusType.endsWith(".REPLACE"))
			{
				typedBonusNameInfo = fullyQualifedBonusType.substring(0, fullyQualifedBonusType.length() - 8);
			}

			// if prefix is of the form:
			// COMBAT.AC
			// then it must match
			// COMBAT.AC
			// COMBAT.AC:Luck
			// COMBAT.AC:Armor.REPLACE
			// However, it must not match
			// COMBAT.ACCHECK
			if ((typedBonusNameInfo.length() > prefix.length()) && !typedBonusNameInfo.startsWith(prefix + ":"))
			{
				continue;
			}

			if (typedBonusNameInfo.startsWith(prefix))
			{
				final int typeIndex = typedBonusNameInfo.indexOf(':');
				if (typeIndex > 0)
				{
					return (fullyQualifedBonusType.substring(typeIndex + 1)); // use aKey to get
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
		activeBonusMap = new ConcurrentHashMap<>();
		cachedActiveBonusSumsMap = new ConcurrentHashMap<>();
		Map<String, String> nonStackMap = new ConcurrentHashMap<>();
		Map<String, String> stackMap = new ConcurrentHashMap<>();
		Set<BonusObj> processedBonuses = Collections.newSetFromMap(new IdentityHashMap<>());

		//Logging.log(Logging.INFO, "=== Start bonus processing.");

		//
		// We do a first pass of just the "static" bonuses
		// as they require less computation and no recursion
		Collection<BonusObj> bonusListCopy = new ArrayList<>(getActiveBonusList());
		for (BonusObj bonus : bonusListCopy)
		{
			if (!bonus.isValueStatic())
			{
				continue;
			}

			final Object source = getSourceObject(bonus);

			if (source == null)
			{
				if (Logging.isDebugMode())
				{
					Logging.debugPrint("BONUS: " + bonus + " ignored due to no creator");
				}
				continue;
			}

			// Keep track of which bonuses have been calculated
			//Logging.log(Logging.INFO, "Processing bonus " + bonus + " - static.");
			processedBonuses.add(bonus);
			for (BonusPair bp : getStringListFromBonus(bonus))
			{
				final double iBonus = bp.resolve(pc).doubleValue();
				setActiveBonusStack(iBonus, bp.fullyQualifiedBonusType, nonStackMap, stackMap);
				totalBonusesForType(nonStackMap, stackMap, bp.fullyQualifiedBonusType, activeBonusMap);

				if (Logging.isDebugMode())
				{
					String id;
					if (source instanceof CDOMObject)
					{
						id = ((CDOMObject) source).getDisplayName();
					}
					else
					{
						id = source.toString();
					}
					Logging.debugPrint("BONUS: " + id + " : " + iBonus + " : " + bp.fullyQualifiedBonusType);
				}
			}
		}

		//
		// Now we do all the BonusObj's that require calculations
		bonusListCopy = new ArrayList<>(getActiveBonusList());
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

			try
			{
				processBonus(bonus, Collections.newSetFromMap(new IdentityHashMap<>()), processedBonuses, nonStackMap,
					stackMap);
			}
			catch (Exception e)
			{
				Logging.errorPrint(e.getLocalizedMessage(), e);
				continue;
			}
		}
	}

	/**
	 * Combines the non-stacking bonus maximum and stacking 
	 * bonus totals to a total bonus for the bonus type. 
	 *  
	 * @param nonStackMap
	 *            The map of non-stacking (i.e. highest wins) bonuses being built up.
	 * @param stackMap
	 *            The map of stacking (i.e. total all) bonuses being built up.
	 * @param fullyQualifiedBonusType
	 *            The type of the bonus e.g. STAT.DEX:LUCK
	 * @param targetMap
	 *            The map of bonuses (stack+non-stack) being built up which will be populated with the total bonus.
	 */
	private static void totalBonusesForType(Map<String, String> nonStackMap, Map<String, String> stackMap,
	                                        String fullyQualifiedBonusType, Map<String, String> targetMap)
	{
		if (fullyQualifiedBonusType != null)
		{
			fullyQualifiedBonusType = fullyQualifiedBonusType.toUpperCase();
		}
		String nonStackString = nonStackMap.get(fullyQualifiedBonusType);
		Float nonStackVal = nonStackString == null ? 0.0f : Float.valueOf(nonStackString);
		String stackString = stackMap.get(fullyQualifiedBonusType);
		Float stackVal = stackString == null ? 0.0f : Float.valueOf(stackString);
		Float FullValue = nonStackVal + stackVal;
		putActiveBonusMap(fullyQualifiedBonusType, String.valueOf(FullValue), targetMap);
	}

	public Collection<BonusObj> getActiveBonusList()
	{
		return activeBonusBySource.keySet();
	}

	public void setActiveBonusList()
	{
		activeBonusBySource = getAllActiveBonuses();
	}

	public String listBonusesFor(String bonusName, String bonusInfo)
	{
		final String prefix = bonusName + '.' + bonusInfo;
		final StringBuilder buf = new StringBuilder();
		final Collection<String> aList = new ArrayList<>();

		// final List<TypedBonus> bonuses = theBonusMap.get(prefix);
		// if ( bonuses == null )
		// {
		// return Constants.EMPTY_STRING;
		// }
		// final List<String> bonusStrings =
		// TypedBonus.totalBonusesByType(bonuses);
		// return CoreUtility.commaDelimit(bonusStrings);

		final Set<String> keys = activeBonusMap.keySet()
		                                       .stream()
		                                       .filter(fullyQualifiedBonusType ->
				                                       fullyQualifiedBonusType.startsWith(prefix))
		                                       .collect(Collectors.toSet());
		for (String fullyQualifiedBonusType : keys)
		{
			// make a list of keys that end with .REPLACE
			if (fullyQualifiedBonusType.endsWith(".REPLACE"))
			{
				aList.add(fullyQualifiedBonusType);
			}
			else
			{
				String reason = "";

				if (fullyQualifiedBonusType.length() > prefix.length())
				{
					reason = fullyQualifiedBonusType.substring(prefix.length() + 1);
				}

				final int b = (int) getActiveBonusForMapKey(fullyQualifiedBonusType, 0);

				if (b == 0)
				{
					continue;
				}

				if (!"NULL".equals(reason) && (!reason.isEmpty()))
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
		for (String fullyQualifiedBonusType_Replace : aList)
		{
			if (fullyQualifiedBonusType_Replace.length() > 7)
			{
				final String aKey =
						fullyQualifiedBonusType_Replace.substring(0, fullyQualifiedBonusType_Replace.length() - 8);
				final double replaceBonus = getActiveBonusForMapKey(fullyQualifiedBonusType_Replace, 0);
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
	 *            stack of calls to processBonus.
	 * @param processedBonuses
	 *            The list of bonuses which have already been processed overall.
	 * @param nonStackMap
	 *            The map of non-stacking (i.e. highest wins) bonuses being built up.
	 * @param stackMap
	 *            The map of stacking (i.e. total all) bonuses being built up.
	 */
	private void processBonus(final BonusObj aBonus, final Set<BonusObj> prevProcessed, Set<BonusObj> processedBonuses,
		Map<String, String> nonStackMap, Map<String, String> stackMap)
	{
		// Make sure we don't get into an infinite loop - can occur due to LST
		// coding or best guess dependancy mapping
		if (prevProcessed.contains(aBonus))
		{
			if (Logging.isDebugMode())
			{
				Logging.log(Logging.DEBUG, "Ignoring bonus loop for " //$NON-NLS-1$
					+ aBonus + " as it was already processed. Bonuses already processed: " //$NON-NLS-1$
					+ prevProcessed);
				Logging.log(Logging.DEBUG, " Depend map is " + aBonus.listDependsMap()); //$NON-NLS-1$
			}
			return;
		}
		prevProcessed.add(aBonus);

		final List<BonusObj> aList = new ArrayList<>();

		// Go through all bonuses and check to see if they add to
		// aBonus's dependencies and have not already been processed
		for (BonusObj newBonus : getActiveBonusList())
		{
			if (processedBonuses.contains(newBonus))
			{
				continue;
			}

			if (aBonus.getDependsOn(newBonus.getUnparsedBonusInfoList())
				|| aBonus.getDependsOnBonusName(newBonus.getBonusName()))
			{
				aList.add(newBonus);
			}
		}

		// go through all the BonusObj's that aBonus depends on
		// and process them first
		for (BonusObj newBonus : aList)
		{
			// Recursively call itself
			processBonus(newBonus, prevProcessed, processedBonuses, nonStackMap, stackMap);
		}

		// Double check that it hasn't been processed yet
		if (processedBonuses.contains(aBonus))
		{
			return;
		}

		// Add to processed list
		//Logging.log(Logging.INFO, "Processing bonus " + aBonus + " depends on " + aBonus.listDependsMap());
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
			setActiveBonusStack(iBonus, bp.fullyQualifiedBonusType, nonStackMap, stackMap);
			totalBonusesForType(nonStackMap, stackMap, bp.fullyQualifiedBonusType, activeBonusMap);
			//			Logging.debugPrint("vBONUS: " + anObj.getDisplayName() + " : "
			//					+ iBonus + " : " + bp.fullyQualifiedBonusType);
		}
		prevProcessed.remove(aBonus);
	}

	/**
	 * Figures out if a bonus should stack based on type, then adds it to the
	 * supplied map.
	 * 
	 * @param bonus
	 *            The value of the bonus.
	 * @param fullyQualifiedBonusType
	 *            The type of the bonus e.g. STAT.DEX:LUCK
	 * @param nonStackbonusMap
	 *            The map of non-stacking (i.e. highest wins) bonuses being built up.
	 * @param stackingBonusMap
	 *            The map of stacking (i.e. total all) bonuses being built up.
	 */
	private static void setActiveBonusStack(double bonus,
	                                        String fullyQualifiedBonusType,
	                                        Map<String, String> nonStackbonusMap,
	                                        Map<String, String> stackingBonusMap)
	{
		if (fullyQualifiedBonusType != null)
		{
			fullyQualifiedBonusType = fullyQualifiedBonusType.toUpperCase();

			// only specific bonuses can actually be fractional
			// -> TODO should define this in external file
			if (!fullyQualifiedBonusType.startsWith("ITEMWEIGHT") && !fullyQualifiedBonusType.startsWith("ITEMCOST")
				&& !fullyQualifiedBonusType.startsWith("ACVALUE") && !fullyQualifiedBonusType.startsWith("ITEMCAPACITY")
				&& !fullyQualifiedBonusType.startsWith("LOADMULT") && !fullyQualifiedBonusType.startsWith("FEAT")
				&& (!fullyQualifiedBonusType.contains("DAMAGEMULT")))
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
		final StringTokenizer aTok = new StringTokenizer(fullyQualifiedBonusType, ":");

		if (aTok.countTokens() == 2)
		{
			// need 2nd token to see if it should stack
			final String aString;
			aTok.nextToken();
			aString = aTok.nextToken();

			if (aString != null)
			{
				index = SettingsHandler.getGame().getUnmodifiableBonusStackList().indexOf(aString); // e.g.
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
		if (fullyQualifiedBonusType.endsWith(".STACK") || fullyQualifiedBonusType.endsWith(".REPLACE"))
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
			final String aVal = nonStackbonusMap.get(fullyQualifiedBonusType);

			if (aVal == null)
			{
				putActiveBonusMap(fullyQualifiedBonusType, String.valueOf(bonus), nonStackbonusMap);
			}
			else
			{
				float existingBonus = Float.parseFloat(aVal);
				putActiveBonusMap(fullyQualifiedBonusType, String.valueOf(Math.max(bonus, existingBonus)),
					nonStackbonusMap);
			}
		}
		else
		// a stacking bonus
		{
			final String aVal = stackingBonusMap.get(fullyQualifiedBonusType);

			if (aVal == null)
			{
				putActiveBonusMap(fullyQualifiedBonusType, String.valueOf(bonus), stackingBonusMap);
			}
			else
			{
				putActiveBonusMap(fullyQualifiedBonusType, String.valueOf(bonus + Float.parseFloat(aVal)),
					stackingBonusMap);
			}
		}
	}

	/**
	 * Put the provided bonus key and value into the supplied bonus map. Some
	 * sanity checking is done on the key.
	 * 
	 * @param fullyQualifiedBonusType
	 *            The bonus key
	 * @param bonusValue
	 *            The value of the bonus
	 * @param bonusMap
	 *            The map of bonuses being built.
	 */
	private static void putActiveBonusMap(final String fullyQualifiedBonusType,
	                                      final String bonusValue,
	                                      Map<String, String> bonusMap)
	{
		//
		// This is a bad idea...will add whatever the bonus is to ALL skills
		//
		if (fullyQualifiedBonusType.equalsIgnoreCase("SKILL.LIST"))
		{
			return;
		}
		bonusMap.put(fullyQualifiedBonusType, bonusValue);
	}

	public int getPartialStatBonusFor(PCStat stat, boolean useTemp, boolean useEquip)
	{
		String statAbbr = stat.getKeyName();
		final String prefix = "STAT." + statAbbr;
		Map<String, String> bonusMap = new HashMap<>();
		Map<String, String> nonStackMap = new ConcurrentHashMap<>();
		Map<String, String> stackMap = new ConcurrentHashMap<>();

		for (BonusObj bonus : getActiveBonusList())
		{
			if (pc.isApplied(bonus) && bonus.getBonusName().equals("STAT"))
			{
				boolean found = false;
				Object co = getSourceObject(bonus);
				for (Object element : bonus.getBonusInfoList())
				{
					if (element instanceof PCStat && element.equals(stat))
					{
						found = true;
						break;
					}
					// TODO: This should be put into a proper object when
					// parisng.
					if (element instanceof MissingObject)
					{
						String name = ((MissingObject) element).getObjectName();
						if (("%LIST".equals(name) || "LIST".equals(name)) && co instanceof CDOMObject)
						{
							CDOMObject creator = (CDOMObject) co;
							for (String assoc : pc.getConsolidatedAssociationList(creator))
							{
								//TODO Case sensitivity?
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
				else if (co instanceof Ability)
				{
					List<String> types = ((Ability) co).getTypes();
					if (types.contains("Equipment"))
					{
						addIt = useEquip;
					}
					else
					{
						addIt = true;
					}
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
						if (bp.fullyQualifiedBonusType.startsWith(prefix))
						{
							setActiveBonusStack(bp.resolve(pc).doubleValue(), bp.fullyQualifiedBonusType, nonStackMap,
								stackMap);
							totalBonusesForType(nonStackMap, stackMap, bp.fullyQualifiedBonusType, bonusMap);
						}
					}
				}
			}
		}
		// Sum the included bonuses to the stat to get our result.
		int total = 0;
		for (final String s : bonusMap.values())
		{
			total += Float.parseFloat(s);
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

	public Map<BonusObj, TempBonusInfo> getTempBonusMap()
	{
		return new IdentityHashMap<>(tempBonusBySource);
	}

	public Map<String, String> getBonuses(String bonusName, String bonusInfo)
	{
		Map<String, String> returnMap = new HashMap<>();
		String prefix = bonusName + "." + bonusInfo + ".";

		for (Map.Entry<String, String> entry : activeBonusMap.entrySet())
		{
			String fullyQualifiedBonusType = entry.getKey();

			if (fullyQualifiedBonusType.startsWith(prefix))
			{
				returnMap.put(fullyQualifiedBonusType, entry.getValue());
			}
		}
		return returnMap;
	}

	public TempBonusInfo addTempBonus(BonusObj bonus, Object source, Object target)
	{
		TempBonusInfo tempBonusInfo = new TempBonusInfo(source, target);
		tempBonusBySource.put(bonus, tempBonusInfo);
		return tempBonusInfo;
	}

	public void removeTempBonus(BonusObj bonus)
	{
		tempBonusBySource.remove(bonus);
	}

	List<String> getNamedTempBonusList()
	{
		final List<String> aList = new ArrayList<>();
		Map<BonusObj, TempBonusInfo> filteredTempBonusList = getFilteredTempBonusList();

		for (Map.Entry<BonusObj, TempBonusInfo> me : filteredTempBonusList.entrySet())
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
		final List<String> aList = new ArrayList<>();
		Map<BonusObj, TempBonusInfo> filteredTempBonusList = getFilteredTempBonusList();

		for (Map.Entry<BonusObj, TempBonusInfo> me : filteredTempBonusList.entrySet())
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

			String aDesc = aCreator.getSafe(StringKey.TEMP_DESCRIPTION);

			if (!aList.contains(aDesc))
			{
				aList.add(aDesc);
			}
		}

		return aList;
	}

	private Map<BonusObj, TempBonusInfo> getFilteredTempBonusList()
	{
		final Map<BonusObj, TempBonusInfo> ret = new IdentityHashMap<>();
		for (Map.Entry<BonusObj, TempBonusInfo> me : tempBonusBySource.entrySet())
		{
			BonusObj bonus = me.getKey();
			TempBonusInfo ti = me.getValue();
			if (!tempBonusFilters.contains(BonusDisplay.getBonusDisplayName(ti)))
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

	private Map<BonusObj, Object> getTempBonuses()
	{
		Map<BonusObj, Object> map = new IdentityHashMap<>();
		getFilteredTempBonusList().forEach((bonus, value) -> {
			pc.setApplied(bonus, false);

			Object source = value.source;
			CDOMObject cdomsource = (source instanceof CDOMObject) ? (CDOMObject) source : null;
			if (bonus.qualifies(pc, cdomsource))
			{
				pc.setApplied(bonus, true);
			}

			if (pc.isApplied(bonus))
			{
				map.put(bonus, source);
			}
		});
		return map;
	}

	public Map<BonusObj, TempBonusInfo> getTempBonusMap(String aCreator, String aTarget)
	{
		final Map<BonusObj, TempBonusInfo> aMap = new IdentityHashMap<>();

		for (Map.Entry<BonusObj, TempBonusInfo> me : tempBonusBySource.entrySet())
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
					sb.append(',');
				}
				sb.append(p.getDescription(shortForm));
				bEmpty = false;
			}
		}

		String type = bo.getTypeString();
		if (!type.isEmpty())
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
		if (!bEmpty)
		{
			sb.append('|');
		}
		sb.append(getSourceString(bo));
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

	public List<BonusPair> getStringListFromBonus(BonusObj bo)
	{
		Object creatorObj = getSourceObject(bo);

		List<String> associatedList;
		CDOMObject anObj = null;
		if (creatorObj instanceof CDOMObject)
		{
			anObj = (CDOMObject) creatorObj;
			associatedList = pc.getConsolidatedAssociationList(anObj);
			if (associatedList == null || associatedList.isEmpty())
			{
				associatedList = NO_ASSOC_LIST;
			}
		}
		else
		{
			associatedList = NO_ASSOC_LIST;
		}

		List<BonusPair> bonusList = new ArrayList<>();

		// Must use getBonusName because it contains the unaltered bonusType
		String bonusName = bo.getBonusName();
		String[] bonusInfoArray = bo.getBonusInfo().split(",");
		String bonusType = bo.getTypeString();

		for (String assoc : associatedList)
		{
			String replacedName;
			if (bonusName.contains(VALUE_TOKEN_REPLACEMENT))
			{
				replacedName = bonusName.replaceAll(VALUE_TOKEN_PATTERN, assoc);
			}
			else
			{
				replacedName = bonusName;
			}
			List<String> replacedInfoList = new ArrayList<>(4);
			for (String bonusInfo : bonusInfoArray)
			{
				if (bonusInfo.contains(VALUE_TOKEN_REPLACEMENT))
				{
					replacedInfoList.add(bonusInfo.replaceAll(VALUE_TOKEN_PATTERN, assoc));
				}
				else if (bonusInfo.contains(VAR_TOKEN_REPLACEMENT))
				{
					replacedInfoList.add(bonusName.replaceAll(VAR_TOKEN_PATTERN, assoc));
				}
				else if (bonusInfo.equals(LIST_TOKEN_REPLACEMENT))
				{
					replacedInfoList.add(assoc);
				}
				else
				{
					replacedInfoList.add(bonusInfo);
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
					thisValue = value.replaceAll(VALUE_TOKEN_PATTERN, assoc);
				}
				//Need to protect against a selection not being made with a %LIST
				if (thisValue.isEmpty())
				{
					thisValue = "0";
				}
				newFormula = FormulaFactory.getFormulaFor(thisValue);
			}
			for (String replacedInfo : replacedInfoList)
			{
				StringBuilder sb = new StringBuilder(100);
				sb.append(replacedName).append('.').append(replacedInfo);
				if (bo.hasTypeString())
				{
					sb.append(':').append(bonusType);
				}
				bonusList.add(new BonusPair(sb.toString(), newFormula, creatorObj));
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

	double calcBonusesWithCost(Iterable<BonusObj> list)
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
				iBonus = aBonus.resolve(pc, anObj.getQualifiedKey()).doubleValue();
			}

			int k;
			if (ChooseActivation.hasNewChooseToken(anObj))
			{
				k = 0;

				for (String aString : pc.getConsolidatedAssociationList(anObj))
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
		return tempBonusBySource.values().stream()
		                        .anyMatch(tbi -> tbi.source.equals(mod));
	}

	private Map<BonusObj, Object> getAllActiveBonuses()
	{
		Map<BonusObj, Object> ret = new IdentityHashMap<>();
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

	/**
	 * Report the change in bonuses from the last checkpoint to the log.
	 */
	public void logChangeFromCheckpoint()
	{
		Map<String, String> addedMap = new HashMap<>(activeBonusMap);
		for (Entry<String, String> prevEntry : checkpointMap.entrySet())
		{
			String addedValue = addedMap.get(prevEntry.getKey());
			if (prevEntry.getValue().equals(addedValue))
			{
				addedMap.remove(prevEntry.getKey());
			}
		}
		Map<String, String> removedMap = new HashMap<>(checkpointMap);
		for (Entry<String, String> prevEntry : activeBonusMap.entrySet())
		{
			String addedValue = removedMap.get(prevEntry.getKey());
			if (prevEntry.getValue().equals(addedValue))
			{
				removedMap.remove(prevEntry.getKey());
			}
		}

		Logging.errorPrint("..Bonuses removed last round: " + removedMap);
		Logging.errorPrint("..Bonuses added last round: " + addedMap);
	}
}

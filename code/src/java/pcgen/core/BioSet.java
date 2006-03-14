/*
 * BioSet.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on September 27, 2002, 5:30 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import pcgen.core.utils.ListKey;
import pcgen.util.Logging;

import java.util.*;

/**
 * <code>BioSet</code>.
 *
 * @author Bryan McRoberts
 * @version $Revision$
 */
public final class BioSet extends PObject
{
	/** key = region.age, value = bonus adjustments. */
	private Map ageMap = new HashMap();

	/** key = Dwarf.BASEAGE or Dwarf%.BASEAGE, value = tagged value. */
	private Map raceMap = new HashMap();

	/** for user overrides/additions, check this before raceMap. */
	private Map userMap = new HashMap();

	/**
	 * Get the age Map
	 * @return ageMap
	 */
	public Map getAgeMap()
	{
		return ageMap;
	}

	/**
	 * @param region
	 * @param index
	 * @return String
	 */
	public String getAgeMapIndex(final String region, final int index)
	{
		String retVal = (String) ageMap.get(region + "." + String.valueOf(index));

		if ((retVal == null) || (retVal.indexOf("BONUS:") < 0))
		{
			retVal = (String) ageMap.get("None." + String.valueOf(index));
		}

		return retVal;
	}

	/**
	 * Get the Age Set line
	 * @param pc
	 * @return age set line
	 */
	public String getAgeSetLine(final PlayerCharacter pc)
	{
		final int ageSet = getPCAgeSet(pc);
		return getAgeMapIndex(pc.getRegion(), ageSet);
	}

	/**
	 * Get the Age Set named
	 * @param ageCategory
	 * @return age set named
	 */
	public int getAgeSetNamed(final String ageCategory)
	{
		String aString;

		for (Iterator e = ageMap.entrySet().iterator(); e.hasNext();)
		{
			final Map.Entry entry = (Map.Entry) e.next();
			aString = entry.getValue().toString();

			if (aString.equals(ageCategory) || aString.startsWith(ageCategory + "\t"))
			{
				aString = entry.getKey().toString();

				final int idx = aString.indexOf('.');

				if (idx >= 0)
				{
					return Integer.parseInt(aString.substring(idx + 1));
				}
			}
		}

		return -1;
	}

	/**
	 * Get the pc Age Set
	 * @param pc
	 * @return age set
	 */
	public int getPCAgeSet(final PlayerCharacter pc)
	{
		final List values = getValueInMaps(pc.getRegion()+ ".", pc.getRace()
			.getName().trim(), ".BASEAGE");

		if (values == null)
		{
			return 0;
		}

		final int pcAge = pc.getAge();
		int ageSet = -1;

		for (Iterator i = values.iterator(); i.hasNext();)
		{
			final int setBaseAge = Integer.parseInt((String) i.next());

			if (pcAge < setBaseAge)
			{
				break;
			}

			++ageSet;
		}

		//
		// Check to see if character is younger than earliest age group
		//
		if (ageSet < 0)
		{
			//Globals.errorPrint("Warning: character is younger than any age information available. Using adjustments for first age category.");
			ageSet = 0;
		}

		return ageSet;
	}

	/**
	 * Builds a string describing the bio settings for the specified race
	 * This string is formatted so that it can be read in by BioSetLoader
	 *
	 * @param region The region of the race to be output
	 * @param race   The name of the race to be output
	 * @return String A lst string describing the region's biosets.
	 */
	public String getRacePCCText(final String region, final String race)
	{
		final StringBuffer sb = new StringBuffer(1000);
		sb.append("REGION:").append(region).append("\n\n");

		final SortedMap ageSets = getRaceTagsByAge(region, race, false);

		return appendAgesetInfo(ageSets, sb);
	}

	/**
	 * Get the tag for the race
	 * @param region
	 * @param race
	 * @param tag
	 * @return List
	 */
	public List getTagForRace(final String region, final String race, final String tag)
	{
		return getValueInMaps(region, race, "." + tag);
	}

	/**
	 * Add the supplied line to the age map. The age map is split by age
	 * brackets (Adulthood, Middle Age etc) and all data loaded must go
	 * into one of these age brackets. When an age line is encountered,
	 * the current age set will be updated and returned.
	 *
	 * @param region = region (e.g. None)
	 * @param line = 0|Adult\tBONUS:STAT|MUS|-1
	 * @param currentAgeSetIndex The age set index to add any data.
	 * @return The new age set index, to be used for later calls to this method.
	 */
	public int addToAgeMap(final String region, final String line, int currentAgeSetIndex)
	{
		final int x = line.indexOf('|');

		if (x >= 0)
		{
			currentAgeSetIndex = Integer.parseInt(line.substring(0, x));
			ageMap.put(region + "." + currentAgeSetIndex, line.substring(x + 1));
		}
		else
		{
			ageMap.put(region + "." + line, null);
		}

		return currentAgeSetIndex;
	}

	/**
	 * Add the supplied line to the race map. The race map contains an array
	 * with an entry for each age set. The supplied index is used to ensure
	 * that the value is placed in the correct age bracket.
	 *
	 * @param region The region the race is defined in.
	 * @param race The race to be updated.
	 * @param tag The tag to be entered. Must be in the form key:value
	 * @param ageSetIndex The age set to be updated.
	 */
	public void addToRaceMap(final String region, final String race, final String tag, final int ageSetIndex)
	{
		addToMap(raceMap, region, race, tag, ageSetIndex);
	}

	/**
	 * Add the supplied line to the user map. The user map contains an array with
	 * an entry for each age set. The supplied index is used to ensure that the
	 * value is placed in the correct age bracket.
	 *
	 * @param region The region the race is defined in.
	 * @param race The race to be updated.
	 * @param tag The tag to be entered. Must be in the form key:value
	 * @param ageSetIndex The age set to be updated.
	 */
	public void addToUserMap(final String region, final String race, final String tag, final int ageSetIndex)
	{
		addToMap(userMap, region, race, tag, ageSetIndex);
	}

	private static void addToMap(final Map map, final String region, final String race, final String tag, final int ageSetIndex)
	{
		final int x = tag.indexOf(':');

		if (x < 0)
		{
			Logging.errorPrint("Invalid value sent to map: " + tag + " (for " + race + ")");

			return; // invalid tag
		}

		final String key = region + "." + race + "." + tag.substring(0, x);
		final String value = tag.substring(x + 1);
		List r = (List) map.get(key);

		if (r == null)
		{
			r = new ArrayList();
		}

		while (r.size() < (ageSetIndex + 1))
		{
			r.add("0");
		}

		r.set(ageSetIndex, value);
		map.put(key, r);
	}

	/**
	 * Clear the user map
	 */
	public void clearUserMap()
	{
		userMap.clear();
	}

	/**
	 * Copies the bio data for one race to a new race.
	 *
	 * @param origRegion The region of the original race
	 * @param origRace   The name of the original race
	 * @param copyRegion The region of the target race
	 * @param copyRace   The name of the target race
	 */
	public void copyRaceTags(final String origRegion, final String origRace, final String copyRegion, final String copyRace)
	{
		// Retreive the original race's info
		final SortedMap ageSets = getRaceTagsByAge(origRegion, origRace, true);

		// Iterate through ages, adding the info for the new race
		for (Iterator it = ageSets.keySet().iterator(); it.hasNext();)
		{
			final Integer key = (Integer) it.next();
			final SortedMap races = (SortedMap) ageSets.get(key);

			for (Iterator raceIt = races.keySet().iterator(); raceIt.hasNext();)
			{
				final String aRaceName = (String) raceIt.next();
				final int currentAgeSetIndex = key.intValue();

				if (!"AGESET".equals(aRaceName))
				{
					final SortedMap tags = (SortedMap) races.get(aRaceName);

					for (Iterator tagIt = tags.keySet().iterator(); tagIt.hasNext();)
					{
						final String tagName = (String) tagIt.next();
						final String value = (String) tags.get(tagName);
						addToUserMap(copyRegion, copyRace, tagName + ":" + value, currentAgeSetIndex);
					}
				}
			}
		}
	}

	/**
	 * Make the kit selection for a pc
	 * @param pc
	 */
	public void makeKitSelectionFor(final PlayerCharacter pc)
	{
		final int ageSet = getPCAgeSet(pc);

		if (pc.hasMadeKitSelectionForAgeSet(ageSet))
		{
			return;
		}

		final String ageSetLine = getAgeMapIndex(pc.getRegion(), ageSet);

		if (ageSetLine == null)
		{
			return;
		}

		final StringTokenizer tok = new StringTokenizer(ageSetLine, "\t", false);
		tok.nextToken(); // name of ageSet e.g. Middle Aged

		final PObject temporaryPObject = new PObject();

		while (tok.hasMoreTokens())
		{
			final String aString = tok.nextToken();

			if (aString.startsWith("KIT:"))
			{
				temporaryPObject.setKitString("0|" + aString.substring(4));
			}
		}

		pc.setArmorProfListStable(false);
		List l = temporaryPObject.getSafeListFor(ListKey.KITS);
		for (int i = 0; i > l.size(); i++)
		{
			KitUtilities.makeKitSelections(0, (String) l.get(i), i, pc);
		}
		pc.setHasMadeKitSelectionForAgeSet(ageSet, true);
	}

	/**
	 * Randomizes the values of the passed in attributes.
	 *
	 * @param randomizeStr .-delimited list of attributes to randomize. (AGE.HT.WT.EYES.HAIR.SKIN are the possible values.)
	 * @param pc The Player Character
	 */
	public void randomize(final String randomizeStr, final PlayerCharacter pc)
	{
		if ((pc == null) || (pc.getRace() == null))
		{
			return;
		}

		final List ranList = new ArrayList();
		final StringTokenizer lineTok = new StringTokenizer(randomizeStr, ".", false);

		while (lineTok.hasMoreTokens())
		{
			final String aString = lineTok.nextToken();

			if (aString.startsWith("AGECAT"))
			{
				generateAge(Integer.parseInt(aString.substring(6)), false, pc);
			}
			else
			{
				ranList.add(aString);
			}
		}

		if (ranList.contains("AGE"))
		{
			generateAge(0, true, pc);
		}

		if (ranList.contains("HT") || ranList.contains("WT"))
		{
			generateHeightWeight(pc);
		}

		if (ranList.contains("EYES"))
		{
			pc.setEyeColor(generateBioValue(".EYES", pc));
		}

		if (ranList.contains("HAIR"))
		{
			pc.setHairColor(generateBioValue(".HAIR", pc));
		}

		if (ranList.contains("SKIN"))
		{
			pc.setSkinColor(generateBioValue(".SKINTONE", pc));
		}
	}

	/**
	 * Remove the user from the map
	 * @param region
	 * @param race
	 * @param tag
	 */
	public void removeFromUserMap(final String region, final String race, final String tag)
	{
		final String key;
		final int x = tag.indexOf(':');

		if (x < 0)
		{
			key = region + "." + race + "." + tag;
		}
		else
		{
			key = region + "." + race + "." + tag.substring(0, x);
		}

		userMap.remove(key);
	}

	public String toString()
	{
		final StringBuffer sb = new StringBuffer(100);
		sb.append("AgeMap: ").append(ageMap.toString()).append("\n");
		sb.append("RaceMap: ").append(raceMap.toString()).append("\n");
		sb.append("UserMap: ").append(userMap.toString()).append("\n");

		return sb.toString();
	}

	private static String replaceString(final String argInput, final String replacement, final int value)
	{
		String input = argInput;
		final int x = input.indexOf(replacement);

		if (x >= 0)
		{
			final String output = input.substring(0, x);
			final String appendage = input.substring(x + replacement.length());
			input = output + value + appendage;
		}

		return input;
	}

	/**
	 * Retrieves a collection of the tags defined for a race grouped by
	 * the age brackets.
	 *
	 * @param region The region of the race
	 * @param race   The name of the race.
	 * @param includeGenericMatches Should generic race references such as Elf% be included
	 * @return SortedMap A map of the gae brackets. Within each age bracket is a
	 * sorted map of the races (one only) and wihtin this is the tags for that
	 * race and age.
	 */
	private SortedMap getRaceTagsByAge(final String region, final String race, final boolean includeGenericMatches)
	{
		String otherRace = "";

		if (includeGenericMatches)
		{
			final int idx = race.indexOf('(');

			if (idx >= 0)
			{
				otherRace = race.substring(0, idx).trim() + '%';
			}
			else
			{
				otherRace = race + '%';
			}
		}

		// Read in ages, setup a mapped structure for them
		final SortedMap ageSets = setupAgeSet(region);

		// Read in the base race settings, split where necessary and add to the appropriate age bracket
		for (Iterator it = raceMap.keySet().iterator(); it.hasNext();)
		{
			final String key = (String) it.next();

			if (key.startsWith(region + "." + race + ".") || key.startsWith(region + "." + otherRace + "."))
			{
				final Object value = raceMap.get(key);
				addTagToAgeSet(ageSets, key, value);
			}
		}

		// Read in the user settings, split where necessary and add to the appropriate age bracket
		for (Iterator it = userMap.keySet().iterator(); it.hasNext();)
		{
			final String key = (String) it.next();

			if (key.startsWith(region + "." + race + ".") || key.startsWith(region + "." + otherRace + "."))
			{
				final Object value = userMap.get(key);
				addTagToAgeSet(ageSets, key, value);
			}
		}

		return ageSets;
	}

	private String getTokenNumberInMaps(final String addKey, final int tokenNum, String regionName, String raceName)
	{
		final List r = getValueInMaps(regionName, raceName, addKey);

		if (r == null)
		{
			return null;
		}

		if (r.size() <= tokenNum)
		{
			return "0";
		}

		return (String) r.get(tokenNum);
	}

	private List getValueInMaps(final String argRegionName, final String argRaceName, final String addKey)
	{
		final String anotherRaceName;

		if (argRaceName.indexOf('(') >= 0)
		{
			anotherRaceName = argRaceName.substring(0, argRaceName.indexOf('(')).trim() + '%';
		}
		else
		{
			anotherRaceName = argRaceName + '%';
		}

		final List r = mapFind(userMap, argRegionName, argRaceName, addKey, anotherRaceName);

		if (r != null)
		{
			return r;
		}

		return mapFind(raceMap, argRegionName, argRaceName, addKey, anotherRaceName);
	}

	/**
	 * Adds the tag (key & value) to the supplied ageSets collection. It is
	 * assumed that the ageSet already has an entry for each age bracket and
	 * that this entry will be a SortedMap of races. Each race will contain a
	 * SortedMap of tags and their values.<br/>
	 * The key is assumed to be of the form region.race.tag
	 * eg "Custom.Human%.MAXAGE"
	 * The value is assumed to be either a list of values or a
	 * single value, depending on the tag. eg "[34,52,69,110]" or "Blond|Brown"
	 * If a single value, it will be added to the first age set. Multiple values
	 * are split amoungst the age sets in order, with any values not matching an
	 * age set being ignored.
	 *
	 * @param ageSets The collection of age brackets.
	 * @param key The region.race.tag specifier.
	 * @param value The value of the tag.
	 */
	private void addTagToAgeSet(final SortedMap ageSets, final String key, final Object value)
	{
		final StringTokenizer tok = new StringTokenizer(key, ".");

		if (tok.countTokens() >= 3)
		{
			tok.nextToken(); // ignore region name

			final String aRaceName = tok.nextToken();
			final String tagName = tok.nextToken();

			if (value instanceof List)
			{
				// Need to split these amoungst the agesets
				// NB: There may be more values than age sets. It seems that there are
				// normally double currently. These extras are not used by this class,
				// so they will not be output, just ignored by this code.
				final List valueList = (List) value;
				final Iterator iter = valueList.iterator();

				for (int ageBracket = 0; (ageBracket < ageSets.size()) && iter.hasNext(); ageBracket++)
				{
					final String tagValue = (String) iter.next();
					final SortedMap races = (SortedMap) ageSets.get(new Integer(ageBracket));
					SortedMap tags = (SortedMap) races.get(aRaceName);

					if (tags == null)
					{
						tags = new TreeMap();
						races.put(aRaceName, tags);
					}

					tags.put(tagName, tagValue);
				}
			}
			else
			{
				final SortedMap races = (SortedMap) ageSets.get(new Integer(0));
				SortedMap tags = (SortedMap) races.get(aRaceName);

				if (tags == null)
				{
					tags = new TreeMap();
					races.put(aRaceName, tags);
				}

				tags.put(tagName, value);
			}
		}
	}

	private String appendAgesetInfo(final SortedMap ageSets, final StringBuffer sb)
	{
		// Iterate through ages, outputing the info
		for (Iterator it = ageSets.keySet().iterator(); it.hasNext();)
		{
			final Integer key = (Integer) it.next();
			final SortedMap races = (SortedMap) ageSets.get(key);

			sb.append("AGESET:").append(key).append("|");
			sb.append(races.get("AGESET")).append("\n");

			for (Iterator raceIt = races.keySet().iterator(); raceIt.hasNext();)
			{
				final String aRaceName = (String) raceIt.next();

				if (!"AGESET".equals(aRaceName))
				{
					final SortedMap tags = (SortedMap) races.get(aRaceName);

					for (Iterator tagIt = tags.keySet().iterator(); tagIt.hasNext();)
					{
						final String tagName = (String) tagIt.next();
						sb.append("RACENAME:").append(aRaceName).append("\t\t");
						sb.append(tagName).append(':').append(tags.get(tagName)).append("\n");
					}
				}
			}

			sb.append("\n");
		}

		return sb.toString();
	}

	private void generateAge(final int ageCategory, final boolean useClassOnly, final PlayerCharacter pc)
	{
		// Can't find a base age for the category,
		// then there's nothing to do
		final String age = getTokenNumberInMaps(".BASEAGE", ageCategory, pc
			.getRegion(), pc.getRace().getName().trim());

		if (age == null)
		{
			return;
		}

		// First check for class age modification information
		final int baseAge = Integer.parseInt(age);
		int ageAdd = -1;

		String aClass = getTokenNumberInMaps(".CLASS", ageCategory, pc
			.getRegion(), pc.getRace().getName().trim());

		if (aClass != null && !aClass.equals("0"))
		{
			// aClass looks like:
			// Barbarian,Rogue,Sorcerer[BASEAGEADD:3d6]|Bard,Fighter,Paladin,Ranger[BASEAGEADD:1d6]
			// So first, get the BASEAGEADD
			final StringTokenizer aTok = new StringTokenizer(aClass, "|");

			while (aTok.hasMoreTokens())
			{
				// String looks like:
				// Barbarian,Rogue,Sorcerer[BASEAGEADD:3d6]
				String aString = aTok.nextToken();

				final int start = aString.indexOf("[");
				final int end = aString.indexOf("]");

				// should be BASEAGEADD:xdy
				String dieString = aString.substring(start + 1, end);

				if (dieString.startsWith("BASEAGEADD:"))
				{
					dieString = dieString.substring(11);
				}

				// Remove the dieString
				aString = aString.substring(0, start);

				final StringTokenizer bTok = new StringTokenizer(aString, ",");

				while (bTok.hasMoreTokens() && (ageAdd < 0))
				{
					final String tClass = bTok.nextToken();

					if (pc.getClassNamed(tClass) != null)
					{
						ageAdd = RollingMethods.roll(dieString);
					}
				}
			}
		}

		// If there was no class age modification,
		// then generate a number based on the .LST
		if ((ageAdd < 0) && !useClassOnly)
		{
			aClass = getTokenNumberInMaps(".AGEDIEROLL", ageCategory, pc
				.getRegion(), pc.getRace().getName().trim());

			if (aClass != null)
			{
				ageAdd = RollingMethods.roll(aClass);
			}
		}

		if ((ageAdd >= 0) && (baseAge > 0))
		{
			final String maxage = getTokenNumberInMaps(".MAXAGE", ageCategory, pc
				.getRegion(), pc.getRace().getName().trim());
			if (maxage != null)
			{
				final int maxAge = Integer.parseInt(maxage);
				if (baseAge + ageAdd > maxAge)
				{
					ageAdd = maxAge-baseAge;
				}
			}
			pc.setAge(baseAge + ageAdd);
		}
	}

	private String generateBioValue(final String addKey, final PlayerCharacter pc)
	{
		final String line = getTokenNumberInMaps(addKey, 0, pc.getRegion(), pc
			.getRace().getName().trim());
		final String rv;

		if (line != null)
		{
			final StringTokenizer aTok = new StringTokenizer(line, "|");
			final List aList = new ArrayList();

			while (aTok.hasMoreTokens())
			{
				aList.add(aTok.nextToken());
			}

			final int roll = RollingMethods.roll(1, aList.size()) - 1; // needs to be 0-offset
			rv = (String) aList.get(roll);
		}
		else
		{
			rv = "";
		}

		return rv;
	}

	private void generateHeightWeight(final PlayerCharacter pc)
	{
		int baseHeight = 0;
		int baseWeight = 0;
		int htAdd = 0;
		int wtAdd = 0;
		String totalWeight = null;
		final String htwt = getTokenNumberInMaps(".SEX", 0, pc.getRegion(), pc
			.getRace().getName().trim());

		if (htwt == null)
		{
			return;
		}

		final StringTokenizer genderTok = new StringTokenizer(htwt, "[]", false);

		while (genderTok.hasMoreTokens())
		{
			if (genderTok.nextToken().equals(pc.getGender()))
			{
				final String htWtLine = genderTok.nextToken();
				final StringTokenizer htwtTok = new StringTokenizer(htWtLine, "|", false);

				while (htwtTok.hasMoreTokens())
				{
					final String tag = htwtTok.nextToken();

					if (tag.startsWith("BASEHT:"))
					{
						baseHeight = Integer.parseInt(tag.substring(7));
					}
					else if (tag.startsWith("BASEWT:"))
					{
						baseWeight = Integer.parseInt(tag.substring(7));
					}
					else if (tag.startsWith("HTDIEROLL:"))
					{
						htAdd = RollingMethods.roll(tag.substring(10));
					}
					else if (tag.startsWith("WTDIEROLL:"))
					{
						wtAdd = RollingMethods.roll(tag.substring(10));
					}
					else if (tag.startsWith("TOTALWT:"))
					{
						totalWeight = tag.substring(8);
					}
				}

				if ((baseHeight != 0) && (htAdd != 0))
				{
					pc.setHeight(baseHeight + htAdd);
				}

				if ((totalWeight != null) && (baseWeight != 0) && (wtAdd != 0))
				{
					totalWeight = replaceString(totalWeight, "HTDIEROLL", htAdd);
					totalWeight = replaceString(totalWeight, "BASEWT", baseWeight);
					totalWeight = replaceString(totalWeight, "WTDIEROLL", wtAdd);
					pc.setWeight(pc.getVariableValue(totalWeight, "").intValue());
				}

				break;
			}
			genderTok.nextToken(); // burn next token
		}
	}

	private List mapFind(final Map argMap, final String argRegionName, final String argRaceName, final String addKey,
	    final String altRaceName)
	{
		// First check for region.racename.key
		String regionName = argRegionName;
		if (!regionName.endsWith("."))
		{
			regionName += ".";
		}

		List r = (List) argMap.get(regionName + argRaceName + addKey);

		if (r != null)
		{
			return r;
		}

		//
		// If not found, try the race name without any parenthesis
		//
		final int altRaceLength = altRaceName.length();

		if (altRaceLength != 0)
		{
			r = (List) argMap.get(regionName + altRaceName + addKey);

			if (r != null)
			{
				return r;
			}
		}

		//
		// If still not found, try the same two searches again without a region
		//
		if (!argRegionName.equals(Constants.s_NONE))
		{
			r = (List) argMap.get(Constants.s_NONE + "." + argRaceName + addKey);

			if (r != null)
			{
				return r;
			}

			if (altRaceLength != 0)
			{
				r = (List) argMap.get(Constants.s_NONE + "." + altRaceName + addKey);
			}
		}

		return r;
	}

	/**
	 * Read in ages, setup a mapped structure for them.
	 * @param region
	 * @return SortedMap
	 */
	private SortedMap setupAgeSet(final String region)
	{
		final SortedMap ageSets = new TreeMap();

		for (Iterator it = ageMap.keySet().iterator(); it.hasNext();)
		{
			final String key = (String) it.next();

			if (key.startsWith(region + "."))
			{
				final Integer setNum = new Integer(key.substring(region.length() + 1));
				final String value = (String) ageMap.get(key);
				final SortedMap races = new TreeMap();
				races.put("AGESET", value);
				ageSets.put(setNum, races);
			}
		}

		return ageSets;
	}
}

/*
 * SystemCollections.java
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
 * Created 2003-07-12 14:02
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pcgen.core.character.EquipSlot;
import pcgen.core.system.LoadInfo;

/**
 * Contains lists of stuff loaded from system-wide lst files.
 *
 * @author     Jonas Karlsson <jujutsunerd@users.sourceforge.net>
 * @version    $Revision$
 **/
public class SystemCollections
{
	/** The following are loaded from system files.
	 * <ul>
	 * <li>alignmentList</li>
	 * <li>birthplaceList</li>
	 * <li>bonusStackList</li>
	 * <li>checkList</li>
	 * <li>cityList</li>
	 * <li>gameModeList</li>
	 * <li>hairStyleList</li>
	 * <li>helpContextFileList</li>
	 * <li>interestsList</li>
	 * <li>locationList</li>
	 * <li>paperInfoList</li>
	 * <li>phobiaList</li>
	 * <li>phraseList</li>
	 * <li>schoolsList</li>
	 * <li>sizeAdjustmentList</li>
	 * <li>equipSlotMap</li>
	 * <li>specialsList</li>
	 * <li>speechList</li>
	 * <li>statList</li>
	 * <li>traitList</li>
	 * <li>bonusSpellMap</li>
	 * <li>paperInfoList</li>
	 * </ul>
	 */
	private static final Map birthplaceMap = new HashMap();
	private static final Map cityMap = new HashMap();
	private static final List gameModeList = new ArrayList();
	private static final Map hairStyleMap = new HashMap();
	private static final Map interestsMap = new HashMap();
	private static final Map locationMap = new HashMap();
	private static final Map paperInfoMap = new HashMap();
	private static final Map phobiaMap = new HashMap();
	private static final Map phraseMap = new HashMap();
	private static final Map speechMap = new HashMap();
	private static final Map traitMap = new HashMap();
	private static final Map equipSlotMap = new HashMap(); // key is the gamemode, value is the list of equipment slots
	private static final Map loadInfoMap = new HashMap(); // key is the gamemode, value is a LoadInfo object
	private static final Map unitSetMap = new HashMap(); // key is the gamemode, value is a HashMap with the unit set name as key and the unit set object als value

	/**
	 * Make sure it doesn't get instantiated.
	 */
	private SystemCollections()
	{
	    // Empty Constructor
	}




	/**
	 * Return a game mode matching the name.
	 * @param aString
	 * @return GameMode
	 */
	public static GameMode getGameModeNamed(final String aString)
	{
		for (Iterator e = gameModeList.iterator(); e.hasNext();)
		{
			final GameMode gameMode = (GameMode) e.next();

			if (gameMode.getName().equalsIgnoreCase(aString))
			{
				return gameMode;
			}
		}

		return null;
	}

	/**
	 * Returns an <b>unmodifiable</b> birtplace list.
	 * @return an <b>unmodifiable</b> birtplace list.
	 */
	public static List getUnmodifiableBirthplaceList()
	{
		List birthplaceList = (List)birthplaceMap.get(SettingsHandler.getGame().getName());
		if (birthplaceList == null)
		{
			birthplaceList = (List)birthplaceMap.get("*");
		}
		if (birthplaceList == null)
		{
			birthplaceList = new ArrayList();
		}
		return Collections.unmodifiableList(birthplaceList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the city list.
	 * @return an <b>unmodifiable</b> version of the city list.
	 */
	public static List getUnmodifiableCityList()
	{
		List cityList = (List)cityMap.get(SettingsHandler.getGame().getName());
		if (cityList == null)
		{
			cityList = (List)cityMap.get("*");
		}
		if (cityList == null)
		{
			cityList = new ArrayList();
		}
		return Collections.unmodifiableList(cityList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the equipment slots list.
	 * @return an <b>unmodifiable</b> version of the equipment slots list.
	 */
	public static List getUnmodifiableEquipSlotList()
	{
		// Try getting an equipslotlist for the currently selected gamemode
		List equipSlotList = (List)equipSlotMap.get(SettingsHandler.getGame().getName());
		if (equipSlotList == null)
		{
			// if that list doesn't exist, try the default equipslotmap
			equipSlotList = (List)equipSlotMap.get("*");
		}
		if (equipSlotList == null)
		{
			// if that's also empty, return an empty list
			equipSlotList = new ArrayList();
		}
		return Collections.unmodifiableList(equipSlotList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the hairstyle list.
	 * @return an <b>unmodifiable</b> version of the hairstyle list.
	 */
	public static List getUnmodifiableGameModeList()
	{
		return Collections.unmodifiableList(gameModeList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the hairstyle list.
	 * @return an <b>unmodifiable</b> version of the hairstyle list.
	 */
	public static List getUnmodifiableHairStyleList()
	{
		List hairStyleList = (List)hairStyleMap.get(SettingsHandler.getGame().getName());
		if (hairStyleList == null)
		{
			hairStyleList = (List)hairStyleMap.get("*");
		}
		if (hairStyleList == null)
		{
			hairStyleList = new ArrayList();
		}
		return Collections.unmodifiableList(hairStyleList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the interests list.
	 * @return an <b>unmodifiable</b> version of the interests list.
	 */
	public static List getUnmodifiableInterestsList()
	{
		List interestsList = (List)interestsMap.get(SettingsHandler.getGame().getName());
		if (interestsList == null)
		{
			interestsList = (List)interestsMap.get("*");
		}
		if (interestsList == null)
		{
			interestsList = new ArrayList();
		}
		return Collections.unmodifiableList(interestsList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the location list.
	 * @return an <b>unmodifiable</b> version of the location list.
	 */
	public static List getUnmodifiableLocationList()
	{
		List locationList = (List)locationMap.get(SettingsHandler.getGame().getName());
		if (locationList == null)
		{
			locationList = (List)locationMap.get("*");
		}
		if (locationList == null)
		{
			locationList = new ArrayList();
		}
		return Collections.unmodifiableList(locationList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the paper info list.
	 * @return an <b>unmodifiable</b> version of the paper info list.
	 */
	public static List getUnmodifiablePaperInfo()
	{
		List paperInfoList = (List)paperInfoMap.get(SettingsHandler.getGame().getName());
		if (paperInfoList == null)
		{
			paperInfoList = (List)paperInfoMap.get("*");
		}
		if (paperInfoList == null)
		{
			paperInfoList = new ArrayList();
		}
		return Collections.unmodifiableList(paperInfoList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the phobia list.
	 * @return an <b>unmodifiable</b> version of the phobia list.
	 */
	public static List getUnmodifiablePhobiaList()
	{
		List phobiaList = (List)phobiaMap.get(SettingsHandler.getGame().getName());
		if (phobiaList == null)
		{
			phobiaList = (List)phobiaMap.get("*");
		}
		if (phobiaList == null)
		{
			phobiaList = new ArrayList();
		}
		return Collections.unmodifiableList(phobiaList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the phrase list.
	 * @return an <b>unmodifiable</b> version of the phrase list.
	 */
	public static List getUnmodifiablePhraseList()
	{
		List phraseList = (List)phraseMap.get(SettingsHandler.getGame().getName());
		if (phraseList == null)
		{
			phraseList = (List)phraseMap.get("*");
		}
		if (phraseList == null)
		{
			phraseList = new ArrayList();
		}
		return Collections.unmodifiableList(phraseList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the speech list.
	 * @return  an <b>unmodifiable</b> version of the speech list.
	 */
	public static List getUnmodifiableSpeechList()
	{
		List speechList = (List)speechMap.get(SettingsHandler.getGame().getName());
		if (speechList == null)
		{
			speechList = (List)speechMap.get("*");
		}
		if (speechList == null)
		{
			speechList = new ArrayList();
		}
		return Collections.unmodifiableList(speechList);
	}


	/**
	 * Return an <b>unmodifiable</b> version of the trait list.
	 * @return an <b>unmodifiable</b> version of the trait list.
	 */
	public static List getUnmodifiableTraitList()
	{
		List traitList = (List)traitMap.get(SettingsHandler.getGame().getName());
		if (traitList == null)
		{
			traitList = (List)traitMap.get("*");
		}
		if (traitList == null)
		{
			traitList = new ArrayList();
		}
		return Collections.unmodifiableList(traitList);
	}

	//BirthplaceList

	/**
	 * Add a birthplace name to the birthplace list.
	 * @param birthplace
	 * @param gameMode
	 */
	public static void addToBirthplaceList(final String birthplace, final String gameMode)
	{
		List birthplaceList = (List)birthplaceMap.get(gameMode);
		if (birthplaceList == null)
		{
			birthplaceList = new ArrayList();
			birthplaceMap.put(gameMode, birthplaceList);
		}
		if (!birthplaceList.contains(birthplace))
		{
			birthplaceList.add(birthplace);
		}
	}

	//CITYLIST

	/**
	 * Add to the city list.
	 * @param city
	 * @param gameMode
	 */
	public static void addToCityList(final String city, final String gameMode)
	{
		List cityList = (List)cityMap.get(gameMode);
		if (cityList == null)
		{
			cityList = new ArrayList();
			cityMap.put(gameMode, cityList);
		}
		if (!cityList.contains(city))
		{
			cityList.add(city);
		}
	}

	//EQUIPSLOTLIST

	/**
	 * Add the equipment slot to the equipment slot list.
	 * @param equipmentSlot
	 * @param gameMode = key in the equipSlotMap to which to add the equipmentSlot
	 */
	public static void addToEquipSlotsList(final EquipSlot equipmentSlot, final String gameMode)
	{
		List equipSlotList = (List)equipSlotMap.get(gameMode);
		if (equipSlotList == null)
		{
			equipSlotList = new ArrayList();
			equipSlotMap.put(gameMode, equipSlotList);
		}
		if (!equipSlotList.contains(equipmentSlot))
		{
			equipSlotList.add(equipmentSlot);
		}
	}

	/**
	 * Get the Load (encumberance) info for the game mode
	 * @param gameMode
	 * @return the Load (encumberance) info for the game mode
	 */
	public static LoadInfo getLoadInfo(final String gameMode)
	{
		LoadInfo loadInfo = (LoadInfo)loadInfoMap.get(gameMode);
		if (loadInfo == null)
		{
			loadInfo = new LoadInfo();
			loadInfoMap.put(gameMode, loadInfo);
		}
		return loadInfo;
	}
	
	/**
	 * Get the Load (encumberance) info for the game mode
	 * @return the Load (encumberance) info for the game mode
	 */
	public static LoadInfo getLoadInfo()
	{
		return (LoadInfo)loadInfoMap.get(SettingsHandler.getGame().getName());
	}

	/**
	 * Get the Unit information for the game mode
	 * @param gameMode
	 * @return the Unit information for the game mode
	 */
	public static HashMap getUnitSetList(final String gameMode)
	{
		HashMap gameUnitSetMap = (HashMap)unitSetMap.get(gameMode);
		if (gameUnitSetMap == null)
		{
			gameUnitSetMap = new HashMap();
			unitSetMap.put(gameMode, gameUnitSetMap);
		}
		return gameUnitSetMap;
	}

	/**
	 * Set the Unit info for the game mode to null
	 * @param gameMode
	 */
	public static void setEmptyUnitSetList(final String gameMode)
	{
		unitSetMap.put(gameMode, null);
	}

	/**
	 * Get the unit info for the current game mode
	 * @return the unit info for the current game mode
	 */
	public static HashMap getUnitSetList()
	{
		return getUnitSetList(SettingsHandler.getGame().getName());
	}

	/**
	 * Get the Unit set info for the game mode
	 * @param unitSetName
	 * @param gameMode
	 * @return the Unit set info for the game mode
	 */
	public static UnitSet getUnitSet(final String unitSetName, String gameMode)
	{
		HashMap gameUnitSetList = (HashMap)unitSetMap.get(gameMode);
		if (gameUnitSetList == null)
		{
			gameUnitSetList = new HashMap();

			// create default Unit Set in case none is specified in the game mode
			UnitSet defaultUnitSet = new UnitSet();
			defaultUnitSet.setName(Constants.s_STANDARD_UNITSET_NAME);
			defaultUnitSet.setHeightUnit(Constants.s_STANDARD_UNITSET_HEIGHTUNIT);
			defaultUnitSet.setHeightFactor(Constants.s_STANDARD_UNITSET_HEIGHTFACTOR);
			defaultUnitSet.setHeightDisplayPattern(Constants.s_STANDARD_UNITSET_HEIGHTDISPLAYPATTERN);
			defaultUnitSet.setDistanceUnit(Constants.s_STANDARD_UNITSET_DISTANCEUNIT);
			defaultUnitSet.setDistanceFactor(Constants.s_STANDARD_UNITSET_DISTANCEFACTOR);
			defaultUnitSet.setDistanceDisplayPattern(Constants.s_STANDARD_UNITSET_DISTANCEDISPLAYPATTERN);
			defaultUnitSet.setWeightUnit(Constants.s_STANDARD_UNITSET_WEIGHTUNIT);
			defaultUnitSet.setWeightFactor(Constants.s_STANDARD_UNITSET_WEIGHTFACTOR);
			defaultUnitSet.setWeightDisplayPattern(Constants.s_STANDARD_UNITSET_WEIGHTDISPLAYPATTERN);
			
			gameUnitSetList.put(Constants.s_STANDARD_UNITSET_NAME, defaultUnitSet);
			unitSetMap.put(gameMode, gameUnitSetList);
		}
	
		UnitSet unitSet = (UnitSet)gameUnitSetList.get(unitSetName);
		if (unitSet == null)
		{
			unitSet = new UnitSet();
			gameUnitSetList.put(unitSetName, unitSet);
		}
		return unitSet;
	}
	
	/**
	 * Get the Unit set info for the game mode by name
	 * @param unitSetName
	 * @param gameMode
	 * @return the Unit set info for the game mode
	 */
	public static UnitSet getUnitSetNamed(final String unitSetName, String gameMode)
	{
		HashMap gameUnitSetList = (HashMap)unitSetMap.get(gameMode);
		if (gameUnitSetList == null)
		{
			return null;
		}
	
		return (UnitSet)gameUnitSetList.get(unitSetName);
	}
		//GAMEMODELIST

	/**
	 * Add the game mode to the list.
	 * @param mode
	 */
	public static void addToGameModeList(final GameMode mode)
	{
		gameModeList.add(mode);
	}

	//HAIRSTYLELIST

	/**
	 * Add the hairstyle to the list.
	 * @param hairStyle
	 * @param gameMode
	 */
	public static void addToHairStyleList(final String hairStyle, final String gameMode)
	{
		List hairStyleList = (List)hairStyleMap.get(gameMode);
		if (hairStyleList == null)
		{
			hairStyleList = new ArrayList();
			hairStyleMap.put(gameMode, hairStyleList);
		}
		if (!hairStyleList.contains(hairStyle))
		{
			hairStyleList.add(hairStyle);
		}
		hairStyleList.add(hairStyle);
	}

	//INTERESTLIST

	/**
	 * Add to the interests list.
	 * @param interest
	 * @param gameMode
	 */
	public static void addToInterestsList(final String interest, final String gameMode)
	{
		List interestsList = (List)interestsMap.get(gameMode);
		if (interestsList == null)
		{
			interestsList = new ArrayList();
			interestsMap.put(gameMode, interestsList);
		}
		if (!interestsList.contains(interest))
		{
			interestsList.add(interest);
		}
	}

	//LOCATIONLIST

	/**
	 * Add to the location list.
	 * @param location
	 * @param gameMode
	 */
	public static void addToLocationList(final String location, final String gameMode)
	{
		List locationList = (List)locationMap.get(gameMode);
		if (locationList == null)
		{
			locationList = new ArrayList();
			locationMap.put(gameMode, locationList);
		}
		if (!locationList.contains(location))
		{
			locationList.add(location);
		}
	}

	//PAPERINFOLIST

	/**
	 * Add the paper info to the list.
	 * @param paper
	 * @param gameMode
	 */
	public static void addToPaperInfoList(final PaperInfo paper, final String gameMode)
	{
		List paperInfoList = (List)paperInfoMap.get(gameMode);
		if (paperInfoList == null)
		{
			paperInfoList = new ArrayList();
			paperInfoMap.put(gameMode, paperInfoList);
		}
		if (!paperInfoList.contains(paper))
		{
			paperInfoList.add(paper);
		}
	}

	//PHOBIALIST

	/**
	 * Add the phobia to the phobia list.
	 * @param phobia
	 * @param gameMode
	 */
	public static void addToPhobiaList(final String phobia, final String gameMode)
	{
		List phobiaList = (List)phobiaMap.get(gameMode);
		if (phobiaList == null)
		{
			phobiaList = new ArrayList();
			phobiaMap.put(gameMode, phobiaList);
		}
		if (!phobiaList.contains(phobia))
		{
			phobiaList.add(phobia);
		}
	}

	//PHRASELIST

	/**
	 * Add the phrase to the phrase list.
	 * @param phrase
	 * @param gameMode
	 */
	public static void addToPhraseList(final String phrase, final String gameMode)
	{
		List phraseList = (List)phraseMap.get(gameMode);
		if (phraseList == null)
		{
			phraseList = new ArrayList();
			phraseMap.put(gameMode, phraseList);
		}
		if (!phraseList.contains(phrase))
		{
			phraseList.add(phrase);
		}
	}

	//SPEECHLIST

	/**
	 * Add to the speech list.
	 * @param speech
	 * @param gameMode
	 */
	public static void addToSpeechList(final String speech, final String gameMode)
	{
		List speechList = (List)speechMap.get(gameMode);
		if (speechList == null)
		{
			speechList = new ArrayList();
			speechMap.put(gameMode, speechList);
		}
		if (!speechList.contains(speech))
		{
			speechList.add(speech);
		}
	}


	//TRAITLIST

	/**
	 * Add the trait to the trait list.
	 * @param trait
	 * @param gameMode
	 */
	public static void addToTraitList(final String trait, final String gameMode)
	{
		List traitList = (List)traitMap.get(gameMode);
		if (traitList == null)
		{
			traitList = new ArrayList();
			traitMap.put(gameMode, traitList);
		}
		if (!traitList.contains(trait))
		{
			traitList.add(trait);
		}

	}

	/**
	 * Empty the equipment slots list.
	 */
	public static void clearEquipSlotsMap()
	{
		equipSlotMap.clear();
	}

	/**
	 * Empty the game mode list.
	 */
	public static void clearGameModeList()
	{
		gameModeList.clear();
	}

	/**
	 * Empty the paper info list.
	 */
	public static void clearPaperInfoList()
	{
		paperInfoMap.clear();
	}

	/**
	 * Sort the game mode list.
	 */
	public static void sortGameModeList()
	{
		Collections.sort(gameModeList);
	}
}

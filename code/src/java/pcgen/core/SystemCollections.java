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
	private static final Map<String, List<String>> birthplaceMap = new HashMap<String, List<String>>();
	private static final Map<String, List<String>> cityMap = new HashMap<String, List<String>>();
	private static final List<GameMode> gameModeList = new ArrayList<GameMode>();
	private static final Map<String, List<String>> hairStyleMap = new HashMap<String, List<String>>();
	private static final Map<String, List<String>> interestsMap = new HashMap<String, List<String>>();
	private static final Map<String, List<String>> locationMap = new HashMap<String, List<String>>();
	private static final Map<String, List<PaperInfo>> paperInfoMap = new HashMap<String, List<PaperInfo>>();
	private static final Map<String, List<String>> phobiaMap = new HashMap<String, List<String>>();
	private static final Map<String, List<String>> phraseMap = new HashMap<String, List<String>>();
	private static final Map<String, List<String>> speechMap = new HashMap<String, List<String>>();
	private static final Map<String, List<String>> traitMap = new HashMap<String, List<String>>();
	private static final Map<String, List<EquipSlot>> equipSlotMap = new HashMap<String, List<EquipSlot>>();
	private static final Map<String, LoadInfo> loadInfoMap = new HashMap<String, LoadInfo>();
	private static final Map<String, Map<String, UnitSet>> unitSetMap = new HashMap<String, Map<String, UnitSet>>();

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
		for ( GameMode gameMode : gameModeList )
		{
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
	public static List<String> getUnmodifiableBirthplaceList()
	{
		List<String> birthplaceList = birthplaceMap.get(SettingsHandler.getGame().getName());
		if (birthplaceList == null)
		{
			birthplaceList = birthplaceMap.get("*");
		}
		if (birthplaceList == null)
		{
			birthplaceList = Collections.emptyList();
		}
		return Collections.unmodifiableList(birthplaceList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the city list.
	 * @return an <b>unmodifiable</b> version of the city list.
	 */
	public static List<String> getUnmodifiableCityList()
	{
		List<String> cityList = cityMap.get(SettingsHandler.getGame().getName());
		if (cityList == null)
		{
			cityList = cityMap.get("*");
		}
		if (cityList == null)
		{
			cityList = Collections.emptyList();
		}
		return Collections.unmodifiableList(cityList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the equipment slots list.
	 * @return an <b>unmodifiable</b> version of the equipment slots list.
	 */
	public static List<EquipSlot> getUnmodifiableEquipSlotList()
	{
		// Try getting an equipslotlist for the currently selected gamemode
		List<EquipSlot> equipSlotList = equipSlotMap.get(SettingsHandler.getGame().getName());
		if (equipSlotList == null)
		{
			// if that list doesn't exist, try the default equipslotmap
			equipSlotList = equipSlotMap.get("*");
		}
		if (equipSlotList == null)
		{
			// if that's also empty, return an empty list
			equipSlotList = Collections.emptyList();
		}
		return Collections.unmodifiableList(equipSlotList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the hairstyle list.
	 * @return an <b>unmodifiable</b> version of the hairstyle list.
	 */
	public static List<GameMode> getUnmodifiableGameModeList()
	{
		return Collections.unmodifiableList(gameModeList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the hairstyle list.
	 * @return an <b>unmodifiable</b> version of the hairstyle list.
	 */
	public static List<String> getUnmodifiableHairStyleList()
	{
		List<String> hairStyleList = hairStyleMap.get(SettingsHandler.getGame().getName());
		if (hairStyleList == null)
		{
			hairStyleList = hairStyleMap.get("*");
		}
		if (hairStyleList == null)
		{
			hairStyleList = Collections.emptyList();
		}
		return Collections.unmodifiableList(hairStyleList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the interests list.
	 * @return an <b>unmodifiable</b> version of the interests list.
	 */
	public static List<String> getUnmodifiableInterestsList()
	{
		List<String> interestsList = interestsMap.get(SettingsHandler.getGame().getName());
		if (interestsList == null)
		{
			interestsList = interestsMap.get("*");
		}
		if (interestsList == null)
		{
			interestsList = Collections.emptyList();
		}
		return Collections.unmodifiableList(interestsList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the location list.
	 * @return an <b>unmodifiable</b> version of the location list.
	 */
	public static List<String> getUnmodifiableLocationList()
	{
		List<String> locationList = locationMap.get(SettingsHandler.getGame().getName());
		if (locationList == null)
		{
			locationList = locationMap.get("*");
		}
		if (locationList == null)
		{
			locationList = Collections.emptyList();
		}
		return Collections.unmodifiableList(locationList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the paper info list.
	 * @return an <b>unmodifiable</b> version of the paper info list.
	 */
	public static List<PaperInfo> getUnmodifiablePaperInfo()
	{
		List<PaperInfo> paperInfoList = paperInfoMap.get(SettingsHandler.getGame().getName());
		if (paperInfoList == null)
		{
			paperInfoList = paperInfoMap.get("*");
		}
		if (paperInfoList == null)
		{
			paperInfoList = Collections.emptyList();
		}
		return Collections.unmodifiableList(paperInfoList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the phobia list.
	 * @return an <b>unmodifiable</b> version of the phobia list.
	 */
	public static List<String> getUnmodifiablePhobiaList()
	{
		List<String> phobiaList = phobiaMap.get(SettingsHandler.getGame().getName());
		if (phobiaList == null)
		{
			phobiaList = phobiaMap.get("*");
		}
		if (phobiaList == null)
		{
			phobiaList = Collections.emptyList();
		}
		return Collections.unmodifiableList(phobiaList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the phrase list.
	 * @return an <b>unmodifiable</b> version of the phrase list.
	 */
	public static List<String> getUnmodifiablePhraseList()
	{
		List<String> phraseList = phraseMap.get(SettingsHandler.getGame().getName());
		if (phraseList == null)
		{
			phraseList = phraseMap.get("*");
		}
		if (phraseList == null)
		{
			phraseList = Collections.emptyList();
		}
		return Collections.unmodifiableList(phraseList);
	}

	/**
	 * Return an <b>unmodifiable</b> version of the speech list.
	 * @return  an <b>unmodifiable</b> version of the speech list.
	 */
	public static List<String> getUnmodifiableSpeechList()
	{
		List<String> speechList = speechMap.get(SettingsHandler.getGame().getName());
		if (speechList == null)
		{
			speechList = speechMap.get("*");
		}
		if (speechList == null)
		{
			speechList = Collections.emptyList();
		}
		return Collections.unmodifiableList(speechList);
	}


	/**
	 * Return an <b>unmodifiable</b> version of the trait list.
	 * @return an <b>unmodifiable</b> version of the trait list.
	 */
	public static List<String> getUnmodifiableTraitList()
	{
		List<String> traitList = traitMap.get(SettingsHandler.getGame().getName());
		if (traitList == null)
		{
			traitList = traitMap.get("*");
		}
		if (traitList == null)
		{
			traitList = Collections.emptyList();
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
		List<String> birthplaceList = birthplaceMap.get(gameMode);
		if (birthplaceList == null)
		{
			birthplaceList = new ArrayList<String>();
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
		List<String> cityList = cityMap.get(gameMode);
		if (cityList == null)
		{
			cityList = new ArrayList<String>();
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
		List<EquipSlot> equipSlotList = equipSlotMap.get(gameMode);
		if (equipSlotList == null)
		{
			equipSlotList = new ArrayList<EquipSlot>();
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
		LoadInfo loadInfo = loadInfoMap.get(gameMode);
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
		return loadInfoMap.get(SettingsHandler.getGame().getName());
	}

	/**
	 * Get the Unit information for the game mode
	 * @param gameMode
	 * @return the Unit information for the game mode
	 */
	public static Map<String, UnitSet> getUnitSetList(final String gameMode)
	{
		Map<String, UnitSet> gameUnitSetMap = unitSetMap.get(gameMode);
		if (gameUnitSetMap == null)
		{
			gameUnitSetMap = new HashMap<String, UnitSet>();
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
	public static Map<String, UnitSet> getUnitSetList()
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
		Map<String, UnitSet> gameUnitSetList = unitSetMap.get(gameMode);
		if (gameUnitSetList == null)
		{
			gameUnitSetList = new HashMap<String, UnitSet>();

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

		UnitSet unitSet = gameUnitSetList.get(unitSetName);
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
		Map<String, UnitSet> gameUnitSetList = unitSetMap.get(gameMode);
		if (gameUnitSetList == null)
		{
			return null;
		}

		return gameUnitSetList.get(unitSetName);
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
		List<String> hairStyleList = hairStyleMap.get(gameMode);
		if (hairStyleList == null)
		{
			hairStyleList = new ArrayList<String>();
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
		List<String> interestsList = interestsMap.get(gameMode);
		if (interestsList == null)
		{
			interestsList = new ArrayList<String>();
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
		List<String> locationList = locationMap.get(gameMode);
		if (locationList == null)
		{
			locationList = new ArrayList<String>();
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
		List<PaperInfo> paperInfoList = paperInfoMap.get(gameMode);
		if (paperInfoList == null)
		{
			paperInfoList = new ArrayList<PaperInfo>();
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
		List<String> phobiaList = phobiaMap.get(gameMode);
		if (phobiaList == null)
		{
			phobiaList = new ArrayList<String>();
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
		List<String> phraseList = phraseMap.get(gameMode);
		if (phraseList == null)
		{
			phraseList = new ArrayList<String>();
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
		List<String> speechList = speechMap.get(gameMode);
		if (speechList == null)
		{
			speechList = new ArrayList<String>();
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
		List<String> traitList = traitMap.get(gameMode);
		if (traitList == null)
		{
			traitList = new ArrayList<String>();
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

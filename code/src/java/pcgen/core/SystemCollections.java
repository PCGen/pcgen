/*
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.core.character.EquipSlot;
import pcgen.core.system.MigrationRule;

/**
 * Contains lists of stuff loaded from system-wide lst files.
 **/
public final class SystemCollections
{
    /**
     * The following are loaded from system files.
     * <ul>
     * <li>birthplaceList</li>
     * <li>bonusStackList</li>
     * <li>cityList</li>
     * <li>gameModeList</li>
     * <li>gameModeDisplayList</li>
     * <li>hairStyleList</li>
     * <li>helpContextFileList</li>
     * <li>interestsList</li>
     * <li>locationList</li>
     * <li>paperInfoList</li>
     * <li>phobiaList</li>
     * <li>phraseList</li>
     * <li>equipSlotMap</li>
     * <li>specialsList</li>
     * <li>speechList</li>
     * <li>traitList</li>
     * <li>paperInfoList</li>
     * </ul>
     */
    private static final Map<String, List<String>> BIRTHPLACE_MAP = new HashMap<>();
    private static final Map<String, List<String>> CITY_MAP = new HashMap<>();
    private static final List<GameMode> GAME_MODE_LIST = new ArrayList<>();
    private static final List<GameModeDisplay> GAME_MODE_DISPLAY_LIST = new ArrayList<>();
    private static final Map<String, List<String>> HAIR_STYLE_MAP = new HashMap<>();
    private static final Map<String, List<String>> INTERESTS_MAP = new HashMap<>();
    private static final Map<String, List<String>> LOCATION_MAP = new HashMap<>();
    private static final Map<String, List<String>> PHOBIA_MAP = new HashMap<>();
    private static final Map<String, Set<String>> PHRASE_MAP = new HashMap<>();
    private static final Map<String, List<String>> SPEECH_MAP = new HashMap<>();
    private static final Map<String, Set<String>> TRAIT_MAP = new HashMap<>();
    private static final Map<String, List<String>> BODY_STRUCTURE_MAP = new HashMap<>();
    private static final Map<String, List<EquipSlot>> EQUIP_SLOT_MAP = new HashMap<>();
    private static final Map<String, List<MigrationRule>> MIGRATION_RULE_MAP = new HashMap<>();

    /**
     * Make sure it doesn't get instantiated.
     */
    private SystemCollections()
    {
        // Empty Constructor
    }

    /**
     * Return a game mode matching the name.
     *
     * @param aString
     * @return GameMode
     */
    public static GameMode getGameModeNamed(final String aString)
    {
        for (GameMode gameMode : GAME_MODE_LIST)
        {
            if (gameMode.getName().equalsIgnoreCase(aString))
            {
                return gameMode;
            }
        }

        return null;
    }

    /**
     * Return a game mode matching the display name.
     *
     * @param aString
     * @return GameMode
     */
    public static GameMode getGameModeWithDisplayName(final String aString)
    {
        for (GameMode gameMode : GAME_MODE_LIST)
        {
            if (gameMode.getDisplayName().equalsIgnoreCase(aString))
            {
                return gameMode;
            }
        }

        return null;
    }

    /**
     * Returns an <b>unmodifiable</b> birtplace list.
     *
     * @return an <b>unmodifiable</b> birtplace list.
     */
    public static List<String> getUnmodifiableBirthplaceList()
    {
        List<String> birthplaceList = BIRTHPLACE_MAP.get(SettingsHandler.getGame().getName());
        if (birthplaceList == null)
        {
            birthplaceList = BIRTHPLACE_MAP.get("*");
        }
        if (birthplaceList == null)
        {
            birthplaceList = Collections.emptyList();
        }
        return Collections.unmodifiableList(birthplaceList);
    }

    /**
     * Return an <b>unmodifiable</b> version of the city list.
     *
     * @return an <b>unmodifiable</b> version of the city list.
     */
    public static List<String> getUnmodifiableCityList()
    {
        List<String> cityList = CITY_MAP.get(SettingsHandler.getGame().getName());
        if (cityList == null)
        {
            cityList = CITY_MAP.get("*");
        }
        if (cityList == null)
        {
            cityList = Collections.emptyList();
        }
        return Collections.unmodifiableList(cityList);
    }

    /**
     * Return an <b>unmodifiable</b> version of the equipment slots list.
     *
     * @return an <b>unmodifiable</b> version of the equipment slots list.
     */
    public static List<EquipSlot> getUnmodifiableEquipSlotList()
    {
        // Try getting an equipslotlist for the currently selected gamemode
        List<EquipSlot> equipSlotList = EQUIP_SLOT_MAP.get(SettingsHandler.getGame().getName());
        if (equipSlotList == null)
        {
            // if that list doesn't exist, try the default equipslotmap
            equipSlotList = EQUIP_SLOT_MAP.get("*");
        }
        if (equipSlotList == null)
        {
            // if that's also empty, return an empty list
            equipSlotList = Collections.emptyList();
        }
        return Collections.unmodifiableList(equipSlotList);
    }

    /**
     * Return an <b>unmodifiable</b> version of the body structure list for the
     * current gamemode.
     *
     * @return an <b>unmodifiable</b> version of the body structure list.
     */
    public static List<String> getUnmodifiableBodyStructureList()
    {
        // Try getting a body structure for the currently selected gamemode
        List<String> bodyStructures = BODY_STRUCTURE_MAP.get(SettingsHandler.getGame().getName());
        if (bodyStructures == null)
        {
            // if that list doesn't exist, try the default body structure list
            bodyStructures = BODY_STRUCTURE_MAP.get("*");
        }
        if (bodyStructures == null)
        {
            // if that's also empty, return an empty list
            bodyStructures = Collections.emptyList();
        }
        return Collections.unmodifiableList(bodyStructures);
    }

    /**
     * Return an <b>unmodifiable</b> version of the migration rules list.
     *
     * @return an <b>unmodifiable</b> version of the migration rules list.
     */
    public static List<MigrationRule> getUnmodifiableMigrationRuleList(String gameModeName)
    {
        // Try getting an migrationRuleList for the currently selected gamemode
        List<MigrationRule> migrationRuleList = MIGRATION_RULE_MAP.get(gameModeName);
        if (migrationRuleList == null)
        {
            // if that list doesn't exist, try the default migrationRuleList
            migrationRuleList = MIGRATION_RULE_MAP.get("*");
        }
        if (migrationRuleList == null)
        {
            // if that's also empty, return an empty list
            migrationRuleList = Collections.emptyList();
        }
        return Collections.unmodifiableList(migrationRuleList);
    }

    /**
     * Return an <b>unmodifiable</b> version of the game mode list.
     *
     * @return an <b>unmodifiable</b> version of the game mode list.
     */
    public static List<GameMode> getUnmodifiableGameModeList()
    {
        return Collections.unmodifiableList(GAME_MODE_LIST);
    }

    /**
     * Return an <b>unmodifiable</b> version of the game mode display list.
     *
     * @return an <b>unmodifiable</b> version of the game mode display list.
     */
    public static List<GameModeDisplay> getUnmodifiableGameModeDisplayList()
    {
        return Collections.unmodifiableList(GAME_MODE_DISPLAY_LIST);
    }

    /**
     * Return an <b>unmodifiable</b> version of the hairstyle list.
     *
     * @return an <b>unmodifiable</b> version of the hairstyle list.
     */
    public static List<String> getUnmodifiableHairStyleList()
    {
        List<String> hairStyleList = HAIR_STYLE_MAP.get(SettingsHandler.getGame().getName());
        if (hairStyleList == null)
        {
            hairStyleList = HAIR_STYLE_MAP.get("*");
        }
        if (hairStyleList == null)
        {
            hairStyleList = Collections.emptyList();
        }
        return Collections.unmodifiableList(hairStyleList);
    }

    /**
     * Return an <b>unmodifiable</b> version of the interests list.
     *
     * @return an <b>unmodifiable</b> version of the interests list.
     */
    public static List<String> getUnmodifiableInterestsList()
    {
        List<String> interestsList = INTERESTS_MAP.get(SettingsHandler.getGame().getName());
        if (interestsList == null)
        {
            interestsList = INTERESTS_MAP.get("*");
        }
        if (interestsList == null)
        {
            interestsList = Collections.emptyList();
        }
        return Collections.unmodifiableList(interestsList);
    }

    /**
     * Return an <b>unmodifiable</b> version of the location list.
     *
     * @return an <b>unmodifiable</b> version of the location list.
     */
    public static List<String> getUnmodifiableLocationList()
    {
        List<String> locationList = LOCATION_MAP.get(SettingsHandler.getGame().getName());
        if (locationList == null)
        {
            locationList = LOCATION_MAP.get("*");
        }
        if (locationList == null)
        {
            locationList = Collections.emptyList();
        }
        return Collections.unmodifiableList(locationList);
    }

    /**
     * Return an <b>unmodifiable</b> version of the phobia list.
     *
     * @return an <b>unmodifiable</b> version of the phobia list.
     */
    public static List<String> getUnmodifiablePhobiaList()
    {
        List<String> phobiaList = PHOBIA_MAP.get(SettingsHandler.getGame().getName());
        if (phobiaList == null)
        {
            phobiaList = PHOBIA_MAP.get("*");
        }
        if (phobiaList == null)
        {
            phobiaList = Collections.emptyList();
        }
        return Collections.unmodifiableList(phobiaList);
    }

    /**
     * Return an <b>unmodifiable</b> version of the phrase list.
     *
     * @return an <b>unmodifiable</b> version of the phrase list.
     */
    public static List<String> getUnmodifiablePhraseList()
    {
        Set<String> phraseSet = PHRASE_MAP.get(SettingsHandler.getGame().getName());
        if (phraseSet == null)
        {
            phraseSet = PHRASE_MAP.get("*");
        }
        if (phraseSet == null)
        {
            return Collections.emptyList();
        }
        return new ArrayList<>(phraseSet);
    }

    /**
     * Return an <b>unmodifiable</b> version of the speech list.
     *
     * @return an <b>unmodifiable</b> version of the speech list.
     */
    public static List<String> getUnmodifiableSpeechList()
    {
        List<String> speechList = SPEECH_MAP.get(SettingsHandler.getGame().getName());
        if (speechList == null)
        {
            speechList = SPEECH_MAP.get("*");
        }
        if (speechList == null)
        {
            speechList = Collections.emptyList();
        }
        return Collections.unmodifiableList(speechList);
    }

    /**
     * Return an <b>unmodifiable</b> version of the trait list.
     *
     * @return an <b>unmodifiable</b> version of the trait list.
     */
    public static List<String> getUnmodifiableTraitList()
    {
        Set<String> traitList = TRAIT_MAP.get(SettingsHandler.getGame().getName());
        if (traitList == null)
        {
            traitList = TRAIT_MAP.get("*");
        }
        if (traitList == null)
        {
            return Collections.emptyList();
        }
        return new ArrayList<>(traitList);
    }

    //BirthplaceList

    /**
     * Add a birthplace name to the birthplace list.
     *
     * @param birthplace
     * @param gameMode
     */
    public static void addToBirthplaceList(final String birthplace, final String gameMode)
    {
        List<String> birthplaceList = BIRTHPLACE_MAP.computeIfAbsent(gameMode, k -> new ArrayList<>());
        if (!birthplaceList.contains(birthplace))
        {
            birthplaceList.add(birthplace);
        }
    }

    //CITYLIST

    /**
     * Add to the city list.
     *
     * @param city
     * @param gameMode
     */
    public static void addToCityList(final String city, final String gameMode)
    {
        List<String> cityList = CITY_MAP.computeIfAbsent(gameMode, k -> new ArrayList<>());
        if (!cityList.contains(city))
        {
            cityList.add(city);
        }
    }

    //EQUIPSLOTLIST

    /**
     * Add the equipment slot to the equipment slot list.
     *
     * @param equipmentSlot
     * @param gameMode      = key in the equipSlotMap to which to add the equipmentSlot
     */
    public static void addToEquipSlotsList(final EquipSlot equipmentSlot, final String gameMode)
    {
        List<EquipSlot> equipSlotList = EQUIP_SLOT_MAP.computeIfAbsent(gameMode, k -> new ArrayList<>());
        if (!equipSlotList.contains(equipmentSlot))
        {
            equipSlotList.add(equipmentSlot);
        }
    }

    /**
     * Add the body structure to the body structure list.
     *
     * @param bodyStructure
     * @param gameMode      = key in the equipSlotMap to which to add the equipmentSlot
     */
    public static void addToBodyStructureList(final String bodyStructure, final String gameMode)
    {
        List<String> bodyStructureList = BODY_STRUCTURE_MAP.computeIfAbsent(gameMode, k -> new ArrayList<>());
        if (!bodyStructureList.contains(bodyStructure))
        {
            bodyStructureList.add(bodyStructure);
        }
    }

    /**
     * Add the migration rule to the game mode's migration rule list.
     *
     * @param migrationRule The migration rule to be added.
     * @param gameMode      = key in the migrationRuleMap to which to add the migrationRule
     */
    public static void addToMigrationRulesList(final MigrationRule migrationRule, final String gameMode)
    {
        List<MigrationRule> migrationRuleList = MIGRATION_RULE_MAP.computeIfAbsent(gameMode, k -> new ArrayList<>());
        if (!migrationRuleList.contains(migrationRule))
        {
            migrationRuleList.add(migrationRule);
        }
    }

    //GAMEMODELIST

    /**
     * Add the game mode to the list.
     *
     * @param mode
     */
    public static void addToGameModeList(final GameMode mode)
    {
        GAME_MODE_LIST.add(mode);
        GAME_MODE_DISPLAY_LIST.add(new GameModeDisplay(mode));
    }

    //HAIRSTYLELIST

    /**
     * Add the hairstyle to the list.
     *
     * @param hairStyle
     * @param gameMode
     */
    public static void addToHairStyleList(final String hairStyle, final String gameMode)
    {
        List<String> hairStyleList = HAIR_STYLE_MAP.computeIfAbsent(gameMode, k -> new ArrayList<>());
        if (!hairStyleList.contains(hairStyle))
        {
            hairStyleList.add(hairStyle);
        }
        hairStyleList.add(hairStyle);
    }

    //INTERESTLIST

    /**
     * Add to the interests list.
     *
     * @param interest
     * @param gameMode
     */
    public static void addToInterestsList(final String interest, final String gameMode)
    {
        List<String> interestsList = INTERESTS_MAP.computeIfAbsent(gameMode, k -> new ArrayList<>());
        if (!interestsList.contains(interest))
        {
            interestsList.add(interest);
        }
    }

    //LOCATIONLIST

    /**
     * Add to the location list.
     *
     * @param location
     * @param gameMode
     */
    public static void addToLocationList(final String location, final String gameMode)
    {
        List<String> locationList = LOCATION_MAP.computeIfAbsent(gameMode, k -> new ArrayList<>());
        if (!locationList.contains(location))
        {
            locationList.add(location);
        }
    }

    //PHOBIALIST

    /**
     * Add the phobia to the phobia list.
     *
     * @param phobia
     * @param gameMode
     */
    public static void addToPhobiaList(final String phobia, final String gameMode)
    {
        List<String> phobiaList = PHOBIA_MAP.computeIfAbsent(gameMode, k -> new ArrayList<>());
        if (!phobiaList.contains(phobia))
        {
            phobiaList.add(phobia);
        }
    }

    //PHRASELIST

    /**
     * Add the phrase to the phrase list.
     *
     * @param phrase
     * @param gameMode
     */
    public static void addToPhraseList(final String phrase, final String gameMode)
    {
        Set<String> phraseList = PHRASE_MAP.computeIfAbsent(gameMode, k -> new HashSet<>());
        phraseList.add(phrase);
    }

    //SPEECHLIST

    /**
     * Add to the speech list.
     *
     * @param speech
     * @param gameMode
     */
    public static void addToSpeechList(final String speech, final String gameMode)
    {
        List<String> speechList = SPEECH_MAP.computeIfAbsent(gameMode, k -> new ArrayList<>());
        if (!speechList.contains(speech))
        {
            speechList.add(speech);
        }
    }

    //TRAITLIST

    /**
     * Add the trait to the trait list.
     *
     * @param trait
     * @param gameMode
     */
    public static void addToTraitList(final String trait, final String gameMode)
    {
        Set<String> traitList = TRAIT_MAP.computeIfAbsent(gameMode, k -> new HashSet<>());
        traitList.add(trait);
    }

    /**
     * Empty the equipment slots list.
     */
    public static void clearEquipSlotsMap()
    {
        EQUIP_SLOT_MAP.clear();
    }

    /**
     * Empty the migration rules list.
     */
    public static void clearMigrationRuleMap()
    {
        MIGRATION_RULE_MAP.clear();
    }

    /**
     * Empty the game mode list.
     */
    public static void clearGameModeList()
    {
        GAME_MODE_LIST.clear();
        GAME_MODE_DISPLAY_LIST.clear();
    }

    /**
     * Sort the game mode list.
     */
    public static void sortGameModeList()
    {
        Collections.sort(GAME_MODE_LIST);
        Collections.sort(GAME_MODE_DISPLAY_LIST);
    }
}

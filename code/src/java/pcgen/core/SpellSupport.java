/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
 * Refactored out of PObject and PCSpellTracker July 23, 2005
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.spell.Spell;
import pcgen.util.DoubleKeyMap;
import pcgen.util.HashMapToList;
import pcgen.core.prereq.Prerequisite;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 *
 * Spell Support object for PObject (offloads code related to spell lists)
 */
public class SpellSupport implements Cloneable
{
	private static final String PIPE = "|";
	private static final String SPELLCASTER = "SPELLCASTER";
	private static final String CLASSSPELLCASTER = "CLASS|SPELLCASTER";
	private static final String ALL = "ALL";

	private HashMap<String, Integer> spellLevelMap = new HashMap<String, Integer>();
	private DoubleKeyMap<String, String, Info> spellInfoMap = new DoubleKeyMap<String, String, Info>();
	private HashMapToList<String, PCSpell> spellMap = new HashMapToList<String, PCSpell>();
	private HashMap<String, List<Prerequisite>> preReqSpellLevelMap = new HashMap<String, List<Prerequisite>>();

	/*
	 * CONSIDER Would eventually like to make this a Collection, and transfer it
	 * to a TreeSet, so that it is automatically sorted... unfortunately, that
	 * is a bit compilacated, since POBject doesn't properly implement the
	 * Comparable interface, and I need to understand when duplicates are legal
	 * in order to properly write my own Comparator.
	 */
	private List<CharacterSpell> characterSpellList = null;

	/**
     * Clear the spell level map 
	 */
    public void clearSpellLevelMap()
	{
		spellLevelMap.clear();
	}

    /**
     * Put a spell level into the map
     * 
     * @param tagType
     * @param className
     * @param spellName
     * @param spellLevel
     */
    public void putLevel(String tagType, String className, String spellName,
			String spellLevel)
	{
		Integer lvl = Integer.valueOf(-1);
		try
		{
			lvl = Integer.parseInt(spellLevel);
			spellLevelMap.put(tagType + "|" + className + "|" + spellName, lvl);
		}
		catch ( NumberFormatException nfe )
		{
            // TODO Handle Exception
		}
	}

	/**
     * Returns true if the map contains the spell level 
     * @param tagType
     * @param className
     * @param spellName
     * @return true if the map contains the spell level
	 */
    public boolean containsLevelFor(String tagType, String className, String spellName)
	{
		return spellLevelMap.containsKey(tagType + "|" + className + "|" + spellName);
	}

	/**
     * Put the spell info into the spell info map
     * 
     * @param tagType
     * @param spellName
     * @param className
     * @param spellLevel
	 */
    public void putInfo(String tagType, String spellName, String className, String spellLevel)
	{
		spellInfoMap.put(tagType, spellName, new Info(className, Integer.parseInt(spellLevel)));
	}

	/**
     * Class that holds spell info
	 */
    public class Info
	{
		/** name */
        public final String name;
		/** spell level */
        public final int level;

        /**
         * Constructor
         * @param n
         * @param l
         */
		public Info(String n, int l)
		{
			name = n;
			level = l;
		}
	}

	/**
     * Clear the spell info map 
	 */
    public void clearSpellInfoMap()
	{
		spellInfoMap.clear();
	}

    /**
     * Returns true if the spell info map contains the info
     * @param string
     * @param spellName
     * @return true if the spell info map contains the info
     */
    public boolean containsInfoFor(String string, String spellName)
	{
		return spellInfoMap.containsKey(string, spellName);
	}

    /**
     * Get the info from the spell ifo map
     * @param string
     * @param spellName
     * @return Info
     */
    public Info getInfo(String string, String spellName)
	{
		return spellInfoMap.get(string, spellName);
	}

	/**
     * Add a list of spells to the spell map 
     * @param level
     * @param aSpellList
	 */
    public void addSpells(final int level, final List<PCSpell> aSpellList)
	{
		final String aLevel = Integer.toString(level);
		for (PCSpell spell : aSpellList )
		{
			if (!spellMap.containsInList(aLevel, spell))
			{
				spellMap.addToListFor(aLevel, spell);
			}
		}
	}

	/**
     * Get a list of spells from the spell map 
     * @param levelLimit
     * @return List of PCSpells
	 */
    public List<PCSpell> getSpellList(int levelLimit)
	{
		boolean allSpells = levelLimit == -1;
		final ArrayList<PCSpell> aList = new ArrayList<PCSpell>();

		if (spellMap != null)
		{
			for ( String key : spellMap.getKeySet() )
			{
				if (allSpells || Integer.parseInt(key.toString()) <= levelLimit)
				{
					aList.addAll(spellMap.getListFor(key));
				}
			}
		}

		return aList;
	}

	/**
	 * Retrieve the list of spells registered for the specific level.
	 *
	 * @param level The level to be retrieved.
	 * @return A List of the level's spells
	 */
	public List<PCSpell> getSpellListForLevel(int level)
	{
		final ArrayList<PCSpell> aList = new ArrayList<PCSpell>();

		if (spellMap != null)
		{
			for ( String key : spellMap.getKeySet() )
			{
				if (Integer.parseInt(key.toString()) == level)
				{
					aList.addAll(spellMap.getListFor(key));
				}
			}
		}

		return aList;
	}

	/**
     * Clear the spell list by initialising a new one 
	 */
    public final void clearSpellList()
	{
		spellMap = new HashMapToList<String, PCSpell>();
	}

    /**
     * Add a spell level to the spell map and spell info into the info map
     * 
     * @param tagType
     * @param className
     * @param spellName
     * @param spellLevel
     * @param preList
     */
	public void addSpellLevel(String tagType, String className, String spellName, String spellLevel, List<Prerequisite> preList)
	{
		preReqSpellLevelMap.put(tagType + "|" + className + "|" + spellName, preList);
		putLevel(tagType, className, spellName, spellLevel);
		putInfo(tagType, spellName, className, spellLevel);
	}

	/**
     * Get the subset of the spell map that passes the PreReq
     *  
     * @param levelMatch
     * @param pc
     * @return Map
	 */
    public Map<String, Integer> getSpellMapPassesPrereqs(int levelMatch, PlayerCharacter pc)
	{
		final Map<String, Integer> tempMap = new HashMap<String, Integer>();

		for ( String key : spellLevelMap.keySet() )
		{
			final int levelInt = spellLevelMap.get(key);

			// levelMatch == -1 means get all spells
			if (((levelMatch == -1) && (levelInt >= 0)) || ((levelMatch >= 0) && (levelInt == levelMatch)))
			{
				if (preReqSpellLevelMap.containsKey(key))
				{
					if (key.startsWith(CLASSSPELLCASTER))
					{
						String spellType = key.substring(18);
						spellType = spellType.substring(0, spellType.indexOf(PIPE));

						if (ALL.equals(spellType) || pc.isSpellCaster(spellType, 1))
						{
							if (PrereqHandler.passesAll(preReqSpellLevelMap.get(key), pc, null))
							{
								for ( PCClass pcClass : pc.getClassList() )
								{
									if (pcClass.getSpellType().equals(spellType) || ALL.equals(spellType))
									{
										StringBuffer tempSb = new StringBuffer();
										tempSb.append(pcClass.getSpellKey())
											.append(PIPE)
											.append(key.substring(key.lastIndexOf(PIPE) + 1));
										tempMap.put(tempSb.toString(), levelInt);
									}
								}
							}
						}
					}
					else if (PrereqHandler.passesAll(preReqSpellLevelMap.get(key), pc, null))
					{
						tempMap.put(key, levelInt);
					}
				}
			}
		}
		return tempMap;
	}

	/**
     * Get the subset Map of infos that meet the PreReq  
     * @param key1
     * @param key2
     * @param pc
     * @return Map
	 */
    public Map<String, Integer> getSpellInfoMapPassesPrereqs(String key1, String key2, PlayerCharacter pc)
	{
		final Map<String, Integer> tempMap = new HashMap<String, Integer>();

		if (spellInfoMap.containsKey(key1, key2))
		{
			Info si = spellInfoMap.get(key1, key2);
			StringBuffer keysb = new StringBuffer();
			keysb.append(key1).append(PIPE).append(si.name).append(PIPE).append(key2);
			String key = keysb.toString();
			if (preReqSpellLevelMap.containsKey(key))
			{
				if (si.name.startsWith(SPELLCASTER))
				{
					final String spellType = si.name.substring(12);

					if (ALL.equals(spellType) || pc.isSpellCaster(spellType, 1))
					{
						if (PrereqHandler.passesAll(preReqSpellLevelMap.get(key), pc, null))
						{
							for ( PCClass pcClass : pc.getClassList() )
							{
								if (pcClass.getSpellType().equals(spellType) || ALL.equals(spellType))
								{
									tempMap.put(pcClass.getSpellKey(), si.level);
								}
							}
						}
					}
				}
				else if (PrereqHandler.passesAll(preReqSpellLevelMap.get(key), pc, null))
				{
					StringBuffer tempSb = new StringBuffer();
					tempSb.append(key1).append(PIPE).append(si.name);
					tempMap.put(tempSb.toString(), si.level);
				}
			}
		}

		return tempMap;
	}

    /**
     * Remove a spell from the character spell list
     * @param spell
     * @return true if removal ok
     */
    final boolean removeCharacterSpell(final CharacterSpell spell)
	{
		if (characterSpellList == null)
		{
			return false;
		}

		return characterSpellList.remove(spell);
	}

	/**
     * Clear the character spell list 
	 */
    final void clearCharacterSpells()
	{
		if ((characterSpellList != null) && !characterSpellList.isEmpty())
		{
			characterSpellList.clear();
		}
	}

	/**
     * Sort the character spell list 
	 */
    public final void sortCharacterSpellList()
	{
		if (characterSpellList != null)
		{
			Collections.sort(characterSpellList);
		}
	}

    /**
     * Add a list of spells to the character spell list
     * @param l
     */
    public final void addAllCharacterSpells(final List<CharacterSpell> l)
	{
		if (characterSpellList == null)
		{
			characterSpellList = new ArrayList<CharacterSpell>();
		}

		characterSpellList.addAll(l);
	}

	/**
     * Add a spell to the character spell list 
     * @param spell
	 */
    public final void addCharacterSpell(final CharacterSpell spell)
	{
		if (characterSpellList == null)
		{
			characterSpellList = new ArrayList<CharacterSpell>();
		}

		characterSpellList.add(spell);
	}

	/**
     * Returns true if the spell is in the character spell list 
     * @param spell
     * @return true if the spell is in the character spell list
	 */
    public final boolean containsCharacterSpell(final CharacterSpell spell)
	{
		return characterSpellList != null && characterSpellList.contains(spell);
	}

	/**
     * Get the number of character spells in the list 
     * @return number of character spells in the list
	 */
    public final int getCharacterSpellCount()
	{
		if (characterSpellList == null)
		{
			return 0;
		}

		return characterSpellList.size();
	}

	/**
     * Get the character spell fromn the character spell list 
     * @param aSpell
     * @param anOwner
     * @return CharacterSpell
	 */
    public final CharacterSpell getCharacterSpellForSpell(final Spell aSpell,
			final PObject anOwner)
	{
		if ((aSpell == null) || (characterSpellList == null))
		{
			return null;
		}

		for ( CharacterSpell cs : characterSpellList )
		{
			final Spell bSpell = cs.getSpell();

			if (aSpell.equals(bSpell) && ((anOwner == null) || cs.getOwner().equals(anOwner)))
			{
				return cs;
			}
		}

		return null;
	}

	/**
     * Get a list of CharacterSpells from the character spell list
     *  
     * @param aSpell
     * @param book
     * @param level
     * @param fList
     * @return list of CharacterSpells from the character spell list
	 */
    public final List<CharacterSpell> getCharacterSpell(final Spell aSpell, final String book,
			final int level, final ArrayList fList)
	{
		final ArrayList<CharacterSpell> aList = new ArrayList<CharacterSpell>();

		if (getCharacterSpellCount() == 0)
		{
			return aList;
		}

		for ( CharacterSpell cs : characterSpellList )
		{
			if ((aSpell == null) || cs.getSpell().equals(aSpell))
			{
				final SpellInfo si = cs.getSpellInfoFor(book, level, -1, fList);

				if (si != null)
				{
					aList.add(cs);
				}
			}
		}

		return aList;
	}

	/**
	 * return an ArrayList of CharacterSpell with following criteria: Spell
	 * aSpell ignored if null book ignored if "" level ignored if < 0 fList
	 * (ignored if null) Array of Feats
	 * @param aSpell
	 * @param book
	 * @param level
	 * @return List
	 */
	public final List<CharacterSpell> getCharacterSpell(final Spell aSpell, final String book, final int level)
	{
		return getCharacterSpell(aSpell, book, level, null);
	}

	/**
     * Get all of the character spells 
     * @return all of the character spells
	 */
    public Collection<CharacterSpell> getCharacterSpellList()
	{
		if (characterSpellList == null)
		{
			return new ArrayList<CharacterSpell>();
		}
		return new ArrayList<CharacterSpell>(characterSpellList);
	}

	public Object clone() throws CloneNotSupportedException {
		SpellSupport ss = (SpellSupport) super.clone();
		ss.spellInfoMap = (DoubleKeyMap<String, String, Info>) spellInfoMap.clone();
		ss.spellMap = new HashMapToList<String, PCSpell>();
		ss.spellMap.addAllLists(spellMap);
		if (characterSpellList != null) {
			ss.characterSpellList = new ArrayList<CharacterSpell>(characterSpellList);
		}
		ss.preReqSpellLevelMap = new HashMap<String, List<Prerequisite>>(preReqSpellLevelMap);
		ss.spellLevelMap = new HashMap<String, Integer>(spellLevelMap);
		return ss;
	}
}

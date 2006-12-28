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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.util.DoubleKeyMap;
import pcgen.util.HashMapToList;

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
	private HashMapToList<Integer, PCSpell> spellMap = new HashMapToList<Integer, PCSpell>();
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
     * @param tagType The type of object to be assigned spells (e.g. CLASS or DOMAIN)
     * @param spellName The name of the spell
     * @return true if the spell info map contains the info
     */
    public boolean containsInfoFor(String tagType, String spellName)
	{
		return spellInfoMap.containsKey(tagType, spellName);
	}

    /**
     * Get the info from the spell ifo map
     * @param tagType The type of object to be assigned spells (e.g. CLASS or DOMAIN)
     * @param spellName The name of the spell
     * @return Info
     */
    public Info getInfo(String tagType, String spellName)
	{
		return spellInfoMap.get(tagType, spellName);
	}

	/**
     * Add a list of spells to the spell map 
     * @param level
     * @param aSpellList
	 */
    public void addSpells(final int level, final List<PCSpell> aSpellList)
	{
    	Integer aLevel = Integer.valueOf(level);
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
			for ( Integer key : spellMap.getKeySet() )
			{
				if (allSpells || key <= levelLimit)
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
			for ( Integer key : spellMap.getKeySet() )
			{
				if (key == level)
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
		spellMap = new HashMapToList<Integer, PCSpell>();
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
     * Remove the spell from the character spell list if it
     * is no longer present in any spell lists.
     * 
     * @param spell The spell to be checked
     * @return True if the spell was removed, false otherwise.
     */
    final public boolean removeSpellIfUnused(final CharacterSpell spell)
    {
		SpellInfo si = spell.getSpellInfoFor("", -1, -1, null);
		if (si == null)
		{
			return removeCharacterSpell(spell);
		}
		return false;
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
			final int level, final ArrayList<Ability> fList)
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
		ss.spellMap = new HashMapToList<Integer, PCSpell>();
		ss.spellMap.addAllLists(spellMap);
		if (characterSpellList != null) {
			ss.characterSpellList = new ArrayList<CharacterSpell>(characterSpellList);
		}
		ss.preReqSpellLevelMap = new HashMap<String, List<Prerequisite>>(preReqSpellLevelMap);
		ss.spellLevelMap = new HashMap<String, Integer>(spellLevelMap);
		return ss;
	}
	
	/**
	 * Get the LST syntax that represents the spells assigned via SPELLLEVEL tags.
	 * @return The LST syntax
	 */
	public String getPCCText()
	{
		StringBuffer txt = new StringBuffer();

		SortedMap<String,StringBuffer> spellOutputMap = new TreeMap<String, StringBuffer>();
		
		// Iterate through the spellinfo map to build the list of spells
		// Outputing any that have prereqs as we can't group them.
		Set<String> tagTypeSet = spellInfoMap.getKeySet();
		for (String tagType : tagTypeSet)
		{
			Set<String> spellNameSet = spellInfoMap.getSecondaryKeySet(tagType);
			for (String spellName : spellNameSet)
			{
				Info spellInfo = spellInfoMap.get(tagType, spellName);
				List<Prerequisite> preReqList = preReqSpellLevelMap.get(tagType + "|" + spellInfo.name + "|" + spellName);
				if (preReqList == null || preReqList.isEmpty())
				{
					StringBuffer key = new StringBuffer();
					key.append(tagType);
					key.append("|").append(spellInfo.name);
					key.append("=").append(spellInfo.level);
					StringBuffer sb = spellOutputMap.get(key.toString());
					if (sb == null)
					{
						sb = new StringBuffer();
						spellOutputMap.put(key.toString(), sb);
					}
					else
					{
						sb.append(',');
					}
					sb.append(spellName);
				}
				else
				{
					if (txt.length() >0)
					{
						txt.append('\t');
					}
					txt.append(getSpellLevelPccText(tagType, spellName, spellInfo, preReqList));
				}
			}
		}
		
		// Iterate through the spellOutputMap outputing the spells
		for (String key : spellOutputMap.keySet())
		{
			if (txt.length() >0)
			{
				txt.append('\t');
			}
			txt.append("SPELLLEVEL:").append(key);
			txt.append("|");
			txt.append(spellOutputMap.get(key).toString());
		}
		
		return txt.toString();
	}

	/**
	 * Build the LST defintiion text for the supplied spell.
     * @param tagType The type of object to be assigned spells (e.g. CLASS or DOMAIN)
     * @param spellName The name of the spell
	 * @param spellInfo The class name and level
	 * @param preReqList The spell's prewrequisites
	 * @return The string representation fo the spell. 
	 */
	private String getSpellLevelPccText(String tagType, String spellName, Info spellInfo, List<Prerequisite> preReqList)
	{
		StringBuffer txt = new StringBuffer();
		txt.append("SPELLLEVEL:").append(tagType);
		txt.append("|").append(spellInfo.name);
		txt.append("=").append(spellInfo.level);
		txt.append("|").append(spellName);
		final StringWriter writer = new StringWriter();
		for (Prerequisite prereq : preReqList)
		{
			final PrerequisiteWriter prereqWriter =
					new PrerequisiteWriter();
			try
			{
				writer.write("|");
				prereqWriter.write(writer, prereq);
			}
			catch (PersistenceLayerException e1)
			{
				e1.printStackTrace();
			}
		}
		txt.append(writer);
		
		return txt.toString();
	}
}

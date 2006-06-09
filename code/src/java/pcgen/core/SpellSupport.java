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
import java.util.Iterator;
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

	private HashMap<String, String> spellLevelMap = new HashMap<String, String>();
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

	public void clearSpellLevelMap()
	{
		spellLevelMap.clear();
	}

	public void putLevel(String tagType, String className, String spellName,
			String spellLevel)
	{
		spellLevelMap.put(tagType + "|" + className + "|" + spellName, spellLevel);
	}

	public boolean containsLevelFor(String tagType, String className, String spellName)
	{
		return spellLevelMap.containsKey(tagType + "|" + className + "|" + spellName);
	}

	public void putInfo(String tagType, String spellName, String className, String spellLevel)
	{
		spellInfoMap.put(tagType, spellName, new Info(className, Integer.parseInt(spellLevel)));
	}

	public class Info
	{
		public final String name;
		public final int level;

		public Info(String n, int l)
		{
			name = n;
			level = l;
		}
	}

	public void clearSpellInfoMap()
	{
		spellInfoMap.clear();
	}

	public boolean containsInfoFor(String string, String spellName)
	{
		return spellInfoMap.containsKey(string, spellName);
	}

	public Info getInfo(String string, String spellName)
	{
		return spellInfoMap.get(string, spellName);
	}

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

	public final void clearSpellList()
	{
		spellMap = new HashMapToList<String, PCSpell>();
	}

	public void addSpellLevel(String tagType, String className, String spellName, String spellLevel, List<Prerequisite> preList)
	{
		preReqSpellLevelMap.put(tagType + "|" + className + "|" + spellName, preList);
		putLevel(tagType, className, spellName, spellLevel);
		putInfo(tagType, spellName, className, spellLevel);
	}

	public Map<String, String> getSpellMapPassesPrereqs(int levelMatch, PlayerCharacter pc)
	{
		final Map<String, String> tempMap = new HashMap<String, String>();

		for ( String key : spellLevelMap.keySet() )
		{
			int levelInt = -1;

			try
			{
				levelInt = Integer.parseInt(spellLevelMap.get(key));
			}
			catch (NumberFormatException nfe)
			{
				// ignored
			}

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
										tempMap.put(tempSb.toString(), Integer.toString(levelInt));
									}
								}
							}
						}
					}
					else if (PrereqHandler.passesAll(preReqSpellLevelMap.get(key), pc, null))
					{
						tempMap.put(key, Integer.toString(levelInt));
					}
				}
			}
		}
		return tempMap;
	}

	public Map<String, String> getSpellInfoMapPassesPrereqs(String key1, String key2, PlayerCharacter pc)
	{
		final Map<String, String> tempMap = new HashMap<String, String>();

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
									tempMap.put(pcClass.getSpellKey(), new Integer(si.level).toString());
								}
							}
						}
					}
				}
				else if (PrereqHandler.passesAll(preReqSpellLevelMap.get(key), pc, null))
				{
					StringBuffer tempSb = new StringBuffer();
					tempSb.append(key1).append(PIPE).append(si.name);
					tempMap.put(tempSb.toString(), new Integer(si.level).toString());
				}
			}
		}

		return tempMap;
	}

	final boolean removeCharacterSpell(final CharacterSpell spell)
	{
		if (characterSpellList == null)
		{
			return false;
		}

		return characterSpellList.remove(spell);
	}

	final void clearCharacterSpells()
	{
		if ((characterSpellList != null) && !characterSpellList.isEmpty())
		{
			characterSpellList.clear();
		}
	}

	public final void sortCharacterSpellList()
	{
		if (characterSpellList != null)
		{
			Collections.sort(characterSpellList);
		}
	}

	public final void addAllCharacterSpells(final List<CharacterSpell> l)
	{
		if (characterSpellList == null)
		{
			characterSpellList = new ArrayList<CharacterSpell>();
		}

		characterSpellList.addAll(l);
	}

	public final void addCharacterSpell(final CharacterSpell spell)
	{
		if (characterSpellList == null)
		{
			characterSpellList = new ArrayList<CharacterSpell>();
		}

		characterSpellList.add(spell);
	}

	public final boolean containsCharacterSpell(final CharacterSpell spell)
	{
		return characterSpellList != null && characterSpellList.contains(spell);
	}

	public final int getCharacterSpellCount()
	{
		if (characterSpellList == null)
		{
			return 0;
		}

		return characterSpellList.size();
	}

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
		ss.spellLevelMap = new HashMap<String, String>(spellLevelMap);
		return ss;
	}
}

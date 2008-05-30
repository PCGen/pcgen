/*
 * PCLevelCastingInfo.java
 * Copyright 2006 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Created: November 8, 2006
 *
 * $Id: PCClass.java 1605 2006-11-08 02:14:21Z thpr $
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;

/**
 * SpellProgressionInfo contains information about Spell Progression in support
 * of a PCClass.
 * 
 * @author Tom Parker <thpr@users.sourceforge.net>
 */
public class SpellProgressionInfo implements Cloneable {

	/*
	 * FUTURETYPESAFETY This should NOT be a String, as Spell Types are a
	 * specific set of items... This, however is NON Trivial, since Spell Types
	 * are used WIDELY through the code base. It will be a nice thing to make
	 * type safe, but it is best done by itself in a checkin specifically
	 * focused on making Spell Types type safe.
	 * 
	 * In the future it would be nice to have this in SpellProgressionInfo, but
	 * that is not possible today because this defaults to 'None' and SOOO much
	 * of the code actually depends on this being non-null. This should be one
	 * of the implicit tasks (moving this) which is part of the project to make
	 * Spell Types Type Safe.
	 */
	private String spellType = null;

	/*
	 * FUTURETYPESAFETY This should really be storing a PCStat or something else
	 * that is type safe, not simply a String.... which is a really great idea,
	 * except for the fact that PCStat actually is SPECIFIC to the
	 * PlayerCharacter, is cloned as a result, and therefore, storing a PCStat
	 * here doesn't store the PCStat from the PlayerCharacter, and is therefore
	 * wrong. Fixing this has a prerequisite of actually 'fixing' PCStat to also
	 * be Involatile.
	 */
	/*
	 * CONSIDER The challenge here is that the bonus spells must be calculated
	 * by the PCClassLevel based on the current Stat (which may change over the
	 * life of a PC and from one PC to another). However, in a literal check,
	 * this variable then only needs to be present in the first PCClassLevel
	 * that can cast any given spell level (otherwise, bonus spells will
	 * unreasonably stack). I think another variable is needed to indicate to a
	 * particular PCClassLevel whether it is allowed to grant bonus spells, and
	 * for what level(s) it grants those bonuses.
	 * 
	 * Note this is dependent upon how PCClassLevel ends up calculating the
	 * known and cast spells for any given level.
	 */
	/**
	 * The abbreviation of the PCStat used to determine the bonus spells for a
	 * spell caster. Is set to DEFAULT, then the bonus spells are determined by
	 * the attribute abbreviated in spellBaseStatAddr.
	 */
	private String bonusSpellBaseStatAbbr = Constants.s_DEFAULT;

	/*
	 * CONSIDER This gets VERY interesting as far as prerequisite checking.
	 * There is a PRESPELLCASTMEMORIZE tag (or some such) that tests how many
	 * classes the character has that memorizes spells. That test gets MUCH more
	 * complicated in a PCClassLevel world, since there will be multiple
	 * PCClassLevels that memorize spells; all of which will use the same
	 * PCClass as a base (therefore does the PREREQ need to keep track of the
	 * matching keys? - I think so, yes.)
	 */
	/**
	 * Indicates if this Spell Progression represents memorized spells.
	 */
	private boolean memorizeSpells = true;

	/**
	 * Indicates the number of KNOWN spells from a Specialty (such as a
	 * SubClass)
	 */
	private int knownSpellsFromSpecialty = 0;

	/**
	 * Sets the Spell Type for this Spell Progression. The type cannot be null
	 * or an empty String. To "unset" the Spell Type, the Spell Type should be
	 * set to Constants.s_NONE
	 * 
	 * @param type
	 *            The type of Spell in this Spell Progression
	 */
	public void setSpellType(String type) {
		if (type == null) {
			throw new IllegalArgumentException("Spell type cannot be null");
		}
		if (type.trim().length() == 0) {
			throw new IllegalArgumentException(
					"Spell type cannot be empty string");
		}
		spellType = type.trim();
	}

	/**
	 * Returns the type of spell in this Spell Progression. Will not return
	 * null. Constants.s_NONE is used to indicate that no Spell Type exists in
	 * this Spell Progression.
	 * 
	 * @return The type of spell in this Spell Progression
	 */
	public String getSpellType() {
		if (spellType == null) {
			return Constants.s_NONE;
		}
		return spellType;
	}

	/**
	 * Used to indicate if this SpellProgression is of memorized spells.
	 * 
	 * @param memorize
	 *            true if this SpellProgression contains spells that are
	 *            memorized; false otherwise.
	 */
	public void setMemorizeSpells(boolean memorize) {
		memorizeSpells = memorize;
	}

	/**
	 * Returns true if this SpellProgression is of memorized spells.
	 * 
	 * @return true if this SpellProgression contains spells that are memorized;
	 *         false otherwise.
	 */
	public boolean memorizesSpells() {
		return memorizeSpells;
	}

	/**
	 * Sets the Bonus Spell Base Spell Stat (using the Stat's abbreviation).
	 * This is an available override of the spell base stat that is only applied
	 * to calculating bonus spells (not to other applications of stats to
	 * spells). If this SpellProgression should use the spell base stat, then
	 * the argument to this method should be Constants.s_DEFAULT
	 * 
	 * @param baseStat
	 *            The abbreviation of the Stat to be used as the Bonus Spell
	 *            Base Spell Stat for this SpellProgression.
	 */
	public void setBonusSpellBaseStatAbbr(String baseStat) {
		if (baseStat == null) {
			throw new IllegalArgumentException(
					"Cannot set Bonus Spell Base Stat to null.  Use s_DEFAULT to reset");
		}
		if (baseStat.trim().length() == 0) {
			throw new IllegalArgumentException(
					"Cannot set Bonus Spell Base Stat to an empty string.  Use s_DEFAULT to reset");
		}
		bonusSpellBaseStatAbbr = baseStat.trim();
	}

	/**
	 * Returns the abbreviation of the Stat used as the Bonus Spell Base Spell
	 * Stat for this SpellProgression. This method will return
	 * Constants.s_DEFAULT if the base spell stat should be used.
	 * 
	 * @return The abbreviation of the Stat used as the Bonus Spell Base Spell
	 *         Stat for this SpellProgression; will be equal to
	 *         Constants.s_DEFAULT if the base spell stat should be used.
	 */
	public String getBonusSpellBaseStatAbbr() {
		return bonusSpellBaseStatAbbr;
	}

	/*
	 * REFACTOR This is a real challenge. This method is actually set from
	 * Domain, as Domain can actually grant domain spells as an addition to the
	 * PCClass. This is a challenge to define how this will actually play out in
	 * the new implementation (When PCClass is static and creates a
	 * PCClassLevel, where do the spells from the Domain come into play?? How
	 * are they added to the character?) Potentially they are added directly to
	 * the PCClassLevel and the refactoring to make PCClassLevel immutable will
	 * have to take place at a later time (As we approach 6.0)
	 */
	/**
	 * Sets the number of KNOWN spells granted by a specialty (Spell School).
	 * Must be greater than or equal to zero.
	 * 
	 * @param anInt
	 *            The number of KNOWN spells granted by a specialty for this
	 *            SpellProgression.
	 */
	public void setKnownSpellsFromSpecialty(int anInt) {
		/*
		 * Zero must be legal to allow (among other things) a .MOD to
		 * override/reset this
		 */
		if (anInt < 0) {
			throw new IllegalArgumentException(
					"Known Spells from Specialty must be greater than or equal to zero");
		}
		knownSpellsFromSpecialty = anInt;
	}

	/**
	 * Returns the number of KNOWN spells granted by a specialty for this Spell
	 * Progression. Will be greater than or equal to zero.
	 * 
	 * @return The number of KNOWN spells granted by a specialty for this Spell
	 *         Progression.
	 */
	public int getKnownSpellsFromSpecialty() {
		return knownSpellsFromSpecialty;
	}

	/**
	 * Clones this SpellProgressionInfo object. A semi-deep (or semi-shallow,
	 * depending on one's point of view) clone is performed, under the
	 * assumption that the cloned object should be allowed to have any of the
	 * SpellProgressionInfo.set* method called without allowing either the
	 * original or the cloned SpellProgressionInfo object to accidentally modify
	 * the other.
	 * 
	 * There is the assumption, however, that the Lists contained within the
	 * SpellProgressionInfo object are never modified, and violation of that
	 * semantic rule either within SpellProgressionInfo or by other objects
	 * which call the reference-semantic methods of SpellProgressionInfo can
	 * render this clone insufficient.
	 * 
	 * @return A semi-shallow Clone of this SpellProgressionInfo object.
	 * @throws CloneNotSupportedException
	 */
	@Override
	public SpellProgressionInfo clone() throws CloneNotSupportedException {
		return (SpellProgressionInfo) super.clone();
	}

	/*
	 * CONSIDER Do I want an "isValid" method to check that spell type is
	 * defined, et al?? - ensure consistency? - thpr 11/9/06
	 */

	/**
	 * Stores an individual Progression within this SpellProgressionInfo. Broken
	 * out as a separate class in order to maintain consistent behavior and
	 * avoid a ton of redundant code within SpellProgressionInfo.
	 */
	private static class Progression implements Cloneable {
		/**
		 * This is a Map of spells. The Integer key is the Class level, the
		 * value is a List of constants or Formula for each SpellLevel.
		 * 
		 * The progressionMap must not contain any null values.
		 */
		private TreeMap<Integer, List<Formula>> progressionMap = null;

		/**
		 * Sets the spells for the given class level for this Progression. The
		 * given character level must be greater than or equal to one.
		 * 
		 * Note that this is a SET (not an ADD) and will therefore OVERWRITE a
		 * spell progression for the given class level if one is already present
		 * within this Progression.
		 * 
		 * @param iLevel
		 *            The class level for which the given spell progression
		 *            applies.
		 * @param aList
		 *            The spell progression for the given class level.
		 * @return The previously set spell progression for the given class
		 *         level; null if no spell progression was previously set.
		 */
		public List<Formula> setProgression(int iLevel, List<Formula> aList) {
			if (iLevel < 1) {
				throw new IllegalArgumentException(
						"Level must be >= 1 in spell progression");
			}
			if (aList == null) {
				throw new IllegalArgumentException(
						"Cannot add null spell progression list to level "
								+ iLevel);
			}
			if (aList.isEmpty()) {
				throw new IllegalArgumentException(
						"Cannot add empty spell progression list to level "
								+ iLevel);
			}
			if (aList.contains(null)) {
				throw new IllegalArgumentException(
						"Cannot have null value in spell progrssion list in level "
								+ iLevel);
			}
			if (progressionMap == null) {
				progressionMap = new TreeMap<Integer, List<Formula>>();
			}
			return progressionMap.put(iLevel, new ArrayList<Formula>(aList));
		}

		public int getMinLevelForSpellLevel(int spellLevel, boolean allowBonus) {
			for (Entry<Integer, List<Formula>> me : progressionMap.entrySet()) {
				List<Formula> progressionList = me.getValue();
				for (int lvl = spellLevel; lvl < progressionList.size(); lvl++) {
					/*
					 * This for loop is to protect against a (admittedly
					 * strange) class that grants N + 1 level spells, but not N
					 * and N is the spellLevel parameter to this method.
					 */
					/*
					 * FIXME This will break if there are spell formulae - thpr
					 * 11/9/06
					 */
					if (allowBonus
							|| Integer.parseInt(progressionList.get(lvl).toString()) != 0) {
						return me.getKey();
					}
				}
			}
			return -1;
		}

		/**
		 * Returns true if this Progression contains a spell progression.
		 * 
		 * @return True if this Progression contains a spell progression; false
		 *         otherwise.
		 */
		public boolean hasProgression() {
			return progressionMap != null;
		}

		/**
		 * Returns the spell progression for this Progression.
		 * 
		 * **WARNING** This method exposes the internal contents of this
		 * Progression object. This method is therefore reference-semantic, and
		 * the returned Map and the Lists contained within the Map should be
		 * considered owned by this Progression. The returned Map and Lists it
		 * contains should not be altered.
		 * 
		 * CONSIDER How to get rid of this - it makes this Object accidentally
		 * mutable. - thpr 11/8/06
		 * 
		 * @return The spell progression for this Progression object.
		 */
		public Map<Integer, List<Formula>> getProgression() {
			if (progressionMap == null) {
				return null;
			}
			return progressionMap;
		}

		/**
		 * Returns the spell progression for the given class level. If this
		 * Progression does not contain a spell progression or if the given
		 * class level is not high enough to have spells, this method returns
		 * null.
		 * 
		 * This method is value-semantic. Ownership of the returned List is
		 * transferred to the calling object (The returned list can be modified
		 * without impacting the internal contents of this Progression)
		 * 
		 * @param classLevel
		 *            The class level for which the spell progression should be
		 *            returned.
		 * @return The spell progression for the given class level, or null if
		 *         there is no spell progression for the given class level.
		 */
		public List<Formula> getProgressionForLevel(int classLevel) {
			List<Formula> spellProgression = null;
			boolean found = false;
			if(progressionMap != null) {
				Integer key = Integer.valueOf(classLevel);
				if (!progressionMap.containsKey(key)) {
					//No spellcasting at level key, check previous levels
					if (progressionMap.firstKey() < classLevel) {
						key = progressionMap.headMap(key).lastKey();
						found = true;
					}
				} else {
					found = true;
				}
				if(found) {
					List<Formula> list = progressionMap.get(key);
					spellProgression = new ArrayList<Formula>(list);
				}
			}
			return spellProgression;
			
		}

		/**
		 * Returns an LST representation of the spell progression in this
		 * Progression.
		 * 
		 * @param lineSep
		 *            The line separator to use for building the LST
		 *            representation of the spell progression.
		 * @return A String (LST) representation of the spell progression.
		 */
		public String getProgressionPCC(String tag, String lineSep) {
			if (lineSep == null) {
				throw new IllegalArgumentException(
						"Line Separator cannot be null");
			}
			if (progressionMap != null) {
				StringBuffer sb = new StringBuffer();
				for (Entry<Integer, List<Formula>> me : progressionMap
						.entrySet()) {
					sb.append(lineSep).append(me.getKey()).append("\t");
					sb.append(tag).append(":");
					sb.append(StringUtil.join(me.getValue(), ","));
				}
				return sb.append(lineSep).toString();
			}
			return null;
		}

		/**
		 * Returns the highest possible spell level in this Progression.
		 * 
		 * Note that this is a theoretical highest level, and based on the
		 * abilities of a PlayerCharacter, the highest spell level may not be
		 * available to that PlayerCharacter.
		 * 
		 * There are at least two known situations where the theoretical spell
		 * level is higher than what a specific PlayerCharacter could actually
		 * know: (1) When a Stat limits the level of spells that a given
		 * PlayerCharacter can learn (2) When a Class grants 0 known spells, but
		 * some PlayerCharacters could have a Stat that provides bonus spells
		 * [this method WILL return a spell level where a Class grants 0 spells,
		 * since this is a theoretical limit test.]
		 * 
		 * @return The highest possible spell level in this Progression.
		 */
		public int getHighestSpellLevel() {
			if (progressionMap != null) {
				int highest = -1;
				for (List<Formula> list : progressionMap.values()) {
					highest = Math.max(highest, list.size() - 1);
				}
				return highest;
			}
			return -1;
		}

		/**
		 * Clones this Progression object. A semi-deep (or semi-shallow,
		 * depending on one's point of view) clone is performed, under the
		 * assumption that the cloned object should be allowed to have the
		 * Progression.set* method called without allowing either the original
		 * or the cloned Progression object to accidentally modify the other.
		 * 
		 * There is the assumption, however, that the Lists contained within the
		 * Progression object are never modified, and violation of that semantic
		 * rule either within Progression or by other objects which call the
		 * reference-semantic methods of Progression can render this clone
		 * insufficient.
		 * 
		 * @return A semi-shallow Clone of this Progression object.
		 * @throws CloneNotSupportedException
		 */
		@Override
		public Progression clone() throws CloneNotSupportedException {
			Progression p = (Progression) super.clone();
			if (progressionMap != null) {
				p.progressionMap = new TreeMap<Integer, List<Formula>>(
						progressionMap);
			}
			return p;
		}
	}
}

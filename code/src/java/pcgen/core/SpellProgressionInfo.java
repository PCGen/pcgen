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
	 * FUTURETYPESAFETY Currently can't do better than String in knownMap,
	 * castMap and specialtyKnownMap, because each one of these can be a
	 * formula, or some special gunk for Psionicists (can we clean up the +d??)
	 */

	/**
	 * This is a Progression of KNOWN spells.
	 */
	private Progression knownProgression = null;

	/**
	 * This is a Progression KNOWN spells added by a specialty.
	 */
	private Progression specialtyKnownProgression = null;

	/**
	 * This is a Progression of CAST spells.
	 */
	private Progression castProgression = null;

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
	 * FUTURETYPESAFETY If this really is a Base stat, then this should be
	 * storing that Stat in a type safe form.... which is a really great idea,
	 * except for the fact that PCStat actually is SPECIFIC to the
	 * PlayerCharacter, is cloned as a result, and therefore, storing a PCStat
	 * here doesn't store the PCStat from the PlayerCharacter, and is therefore
	 * wrong. Fixing this has a prerequisite of actually 'fixing' PCStat to also
	 * be Involatile.
	 */
	/**
	 * This is the abbreviation of the base PCStat used to the attribute on
	 * which a Spell caster casts spells. It may also indicate the attribute
	 * used to determine bonus spells, if bonusSpellBaseStatAbbr is set to
	 * DEFAULT
	 */
	private String spellBaseStatAbbr = Constants.s_NONE;

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

	/*
	 * CONSIDER This gets VERY interesting as far as prerequisite checking.
	 * There is a PRESPELLBOOKTESTER tag (or some such) that tests how many
	 * classes the character has that memorizes spells. That test gets MUCH more
	 * complicated in a PCClassLevel world, since there will be multiple
	 * PCClassLevels that use a spell book; all of which will use the same
	 * PCClass as a base (therefore does the PREREQ need to keep track of the
	 * matching keys?)
	 */
	/**
	 * Indicates if this SpellProgression is associated with using a Spell Book
	 */
	private boolean usesSpellBook = false;

	/**
	 * Indicates the number of KNOWN spells from a Specialty (such as a
	 * SubClass)
	 */
	private int knownSpellsFromSpecialty = 0;

	/**
	 * Sets the Known spells for the given class level for this
	 * SpellProgression. The given character level must be greater than or equal
	 * to one.
	 * 
	 * Note that this is a SET (not an ADD) and will therefore OVERWRITE a KNOWN
	 * spell progression for the given class level if one is already present
	 * within this SpellProgression.
	 * 
	 * @param iLevel
	 *            The class level for which the given known spell progression
	 *            applies.
	 * @param aList
	 *            The known spell progression for the given class level.
	 * @return The previously set KNOWN spell progression for the given class
	 *         level; null if no KNOWN spell progression was previously set.
	 */
	public List<Formula> setKnown(int iLevel, List<Formula> aList) {
		if (knownProgression == null) {
			knownProgression = new Progression();
		}
		return knownProgression.setProgression(iLevel, aList);
	}

	/**
	 * Returns true if this SpellProgression contains a KNOWN spell progression.
	 * (this is not required, e.g. OGL Wizards do not have a KNOWN spell
	 * progression, rather they are limited by what spells are in their
	 * spellbook(s))
	 * 
	 * @return True if this SpellProgression contains a known spell progression;
	 *         false otherwise.
	 */
	public boolean hasKnownProgression() {
		return knownProgression != null && knownProgression.hasProgression();
	}

	/**
	 * Returns the known spell progression for this SpellProgression.
	 * 
	 * **WARNING** This method exposes the internal contents of this
	 * SpellProgression object. This method is therefore reference-semantic, and
	 * the returned Map and the Lists contained within the Map should be
	 * considered owned by this SpellProgression. The returned Map and Lists it
	 * contains should not be altered.
	 * 
	 * CONSIDER How to get rid of this - it makes this Object accidentally
	 * mutable. - thpr 11/8/06
	 * 
	 * @return The known spell progression for this SpellProgression object.
	 */
	public Map<Integer, List<Formula>> getKnownProgression() {
		return knownProgression == null ? null : knownProgression
				.getProgression();
	}

	/**
	 * Returns the known spell progression for the given class level. If this
	 * SpellProgression does not contain a KNOWN spell progression or if the
	 * given class level is not high enough to have known spells, this method
	 * returns null.
	 * 
	 * This method is value-semantic. Ownership of the returned List is
	 * transferred to the calling object (The returned list can be modified
	 * without impacting the internal contents of this SpellProgression)
	 * 
	 * @param aInt
	 *            The class level for which the known spell progression should
	 *            be returned.
	 * @return The known spell progression for the given class level, or null if
	 *         there is no known spell progression for the given class level.
	 */
	public List<Formula> getKnownForLevel(int aLevel) {
		return knownProgression == null ? null : knownProgression
				.getProgressionForLevel(aLevel);
	}

	/**
	 * Returns an LST representation of the KNOWN spell progression in this
	 * SpellProgression.
	 * 
	 * @param lineSep
	 *            The line separator to use for building the LST representation
	 *            of the KNOWN spell progression.
	 * @return A String (LST) representation of the KNOWN spell progression.
	 */
	public String getKnownPCC(String lineSep) {
		return knownProgression == null ? null : knownProgression
				.getProgressionPCC("KNOWN", lineSep);
	}

	/**
	 * Returns the highest possible known spell level in this SpellProgression.
	 * 
	 * Note that this is a theoretical highest level, and based on the abilities
	 * of a PlayerCharacter, the highest known spell level may not be available
	 * to that PlayerCharacter.
	 * 
	 * There are at least two known situations where the theoretical known spell
	 * level is higher than what a specific PlayerCharacter could actually know:
	 * (1) When a Stat limits the level of spells that a given PlayerCharacter
	 * can learn (2) When a Class grants 0 known spells, but some
	 * PlayerCharacters could have a Stat that provides bonus spells [this
	 * method WILL return a spell level where a Class grants 0 spells, since
	 * this is a theoretical limit test.]
	 * 
	 * @return The highest possible known spell level.
	 */
	public int getHighestKnownSpellLevel() {
		return knownProgression == null ? 0 : knownProgression
				.getHighestSpellLevel();
	}

	/**
	 * Sets the KNOWN SPECIALTY spells for the given class level for this
	 * SpellProgression. The given character level must be greater than or equal
	 * to one.
	 * 
	 * Note that this is a SET (not an ADD) and will therefore OVERWRITE a KNOWN
	 * SPECIALTY spell progression for the given class level if one is already
	 * present within this SpellProgression.
	 * 
	 * @param iLevel
	 *            The class level for which the given known specialty spell
	 *            progression applies.
	 * @param aList
	 *            The known specialty spell progression for the given class
	 *            level.
	 * @return The previously set KNOWN SPECIALTY spell progression for the
	 *         given class level; null if no KNOWN SPECIALTY spell progression
	 *         was previously set.
	 */
	public List<Formula> setSpecialtyKnown(int aLevel, List<Formula> aList) {
		if (specialtyKnownProgression == null) {
			specialtyKnownProgression = new Progression();
		}
		return specialtyKnownProgression.setProgression(aLevel, aList);
	}

	/**
	 * Returns true if this SpellProgression contains KNOWN SPECIALTY spell
	 * progressions. (this is not required, e.g. most 3.0SRD classes do not have
	 * a KNOWN SPECIALTY list)
	 * 
	 * @return True if this SpellProgression contains a known specialty spell
	 *         progression; false otherwise.
	 */
	public boolean hasSpecialtyKnownProgression() {
		return specialtyKnownProgression != null
				&& specialtyKnownProgression.hasProgression();
	}

	/**
	 * Returns the known specialty spell progression for this SpellProgression.
	 * 
	 * **WARNING** This method exposes the internal contents of this
	 * SpellProgression object. This method is therefore reference-semantic, and
	 * the returned Map and the Lists contained within the Map should be
	 * considered owned by this SpellProgression. The returned Map and Lists it
	 * contains should not be altered.
	 * 
	 * CONSIDER How to get rid of this - it makes this Object accidentally
	 * mutable. - thpr 11/8/06
	 * 
	 * @return The known specialty spell progression for this SpellProgression
	 *         object.
	 */
	public Map<Integer, List<Formula>> getSpecialtyKnownMap() {
		return specialtyKnownProgression == null ? null
				: specialtyKnownProgression.getProgression();
	}

	/**
	 * Returns the known specialty spell progression for the given class level.
	 * If this SpellProgression does not contain a KNOWN SPECIALTY spell
	 * progression or if the given class level is not high enough to have
	 * entered the known specialty spell progression, this method returns null.
	 * 
	 * This method is value-semantic. Ownership of the returned List is
	 * transferred to the calling object (The returned list can be modified
	 * without impacting the internal contents of this SpellProgression)
	 * 
	 * @param aInt
	 *            The class level for which the known specialty spell
	 *            progression should be returned.
	 * @return The known specialty spell progression for the given class level,
	 *         or null if there is no known specialty spell progression for the
	 *         given class level.
	 */
	public List<Formula> getSpecialtyKnownForLevel(int aLevel) {
		return specialtyKnownProgression == null ? null
				: specialtyKnownProgression.getProgressionForLevel(aLevel);
	}

	/**
	 * Returns an LST representation of the KNOWN SPECIALTY spell progression in
	 * this SpellProgression.
	 * 
	 * @param lineSep
	 *            The line separator to use for building the LST representation
	 *            of the KNOWN SPECIALTY spell progression.
	 * @return A String (LST) representation of the KNOWN SPECIALTY spell
	 *         progression.
	 */
	public String getSpecialtyKnownPCC(String lineSep) {
		return specialtyKnownProgression == null ? null
				: specialtyKnownProgression.getProgressionPCC("SPECIALTYKNOWN",
						lineSep);
	}

	/**
	 * Sets the CAST spells for the given class level for this SpellProgression.
	 * The given character level must be greater than or equal to one.
	 * 
	 * Note that this is a SET (not an ADD) and will therefore OVERWRITE a CAST
	 * spell progression for the given class level if one is already present
	 * within this SpellProgression.
	 * 
	 * @param iLevel
	 *            The class level for which the given CAST spell progression
	 *            applies.
	 * @param aList
	 *            The CAST spell progression for the given class level.
	 * @return The previously set CAST spell progression for the given class
	 *         level; null if no CAST spell progression was previously set.
	 */
	public List<Formula> setCast(int aLevel, List<Formula> aList) {
		if (castProgression == null) {
			castProgression = new Progression();
		}
		return castProgression.setProgression(aLevel, aList);
	}

	/**
	 * Returns true if this SpellProgression contains CAST spell progressions.
	 * (this is not required, but would be a bit strange to be empty)
	 * 
	 * @return True if this SpellProgression contains a CAST spell progression;
	 *         false otherwise.
	 */
	public boolean hasCastProgression() {
		return castProgression != null && castProgression.hasProgression();
	}

	/**
	 * Returns the CAST spell progression for this SpellProgression.
	 * 
	 * **WARNING** This method exposes the internal contents of this
	 * SpellProgression object. This method is therefore reference-semantic, and
	 * the returned Map and the Lists contained within the Map should be
	 * considered owned by this SpellProgression. The returned Map and Lists it
	 * contains should not be altered.
	 * 
	 * CONSIDER How to get rid of this - it makes this Object accidentally
	 * mutable. - thpr 11/8/06
	 * 
	 * @return The CAST spell progression for this SpellProgression object.
	 */
	public Map<Integer, List<Formula>> getCastProgression() {
		return castProgression == null ? null : castProgression
				.getProgression();
	}

	/**
	 * Returns the CAST spell progression for the given class level. If this
	 * SpellProgression does not contain a CAST spell progression or if the
	 * given class level is not high enough to have CAST spells, this method
	 * returns null.
	 * 
	 * This method is value-semantic. Ownership of the returned List is
	 * transferred to the calling object (The returned list can be modified
	 * without impacting the internal contents of this SpellProgression)
	 * 
	 * @param aInt
	 *            The class level for which the CAST spell progression should be
	 *            returned.
	 * @return The CAST spell progression for the given class level, or null if
	 *         there is no CAST spell progression for the given class level.
	 */
	public List<Formula> getCastForLevel(int aLevel) {
		return castProgression == null ? null : castProgression
				.getProgressionForLevel(aLevel);
	}

	/**
	 * Returns the highest possible CAST spell level in this SpellProgression.
	 * 
	 * Note that this is a theoretical highest level, and based on the abilities
	 * of a PlayerCharacter, the highest CAST spell level may not be available
	 * to that PlayerCharacter.
	 * 
	 * There are at least two known situations where the theoretical CAST spell
	 * level is higher than what a specific PlayerCharacter could actually know:
	 * (1) When a Stat limits the level of spells that a given PlayerCharacter
	 * can learn (2) When a Class grants 0 CAST spells, but some
	 * PlayerCharacters could have a Stat that provides bonus spells [this
	 * method WILL return a spell level where a Class grants 0 spells, since
	 * this is a theoretical limit test.]
	 * 
	 * @return The highest possible CAST spell level.
	 */
	public int getHighestCastSpellLevel() {
		return castProgression == null ? 0 : castProgression
				.getHighestSpellLevel();
	}

	/**
	 * Returns an LST representation of the CAST spell progression in this
	 * SpellProgression.
	 * 
	 * @param lineSep
	 *            The line separator to use for building the LST representation
	 *            of the CAST spell progression.
	 * @return A String (LST) representation of the CAST spell progression.
	 */
	public String getCastPCC(String lineSep) {
		return castProgression == null ? null : castProgression
				.getProgressionPCC("CAST", lineSep);
	}

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
	 * Used to indicate if the spells in this SpellProgression use spell books.
	 * 
	 * @param usesBook
	 *            true if the spells in this SpellProgression use spell books;
	 *            false otherwise.
	 */
	public void setSpellBookUsed(boolean usesBook) {
		usesSpellBook = usesBook;
	}

	/**
	 * Returns true if the spells in this SpellProgression use spell books.
	 * 
	 * @return true if the spells in this SpellProgression use spell books;
	 *         false otherwise.
	 */
	public boolean usesSpellBook() {
		return usesSpellBook;
	}

	/**
	 * Sets the Base Spell Stat (using the Stat's abbreviation)
	 * 
	 * @param baseStat
	 *            The abbreviation of the Stat to be used as the base spell stat
	 *            for this SpellProgression.
	 */
	public void setSpellBaseStatAbbr(String baseStat) {
		/*
		 * CONSIDER This null test today CANNOT be performed, because a null set
		 * is AUTOMATICALLY performed by a SubClass or SubstitutionClass. - thpr
		 * 11/9/06
		 * 
		 * if (baseStat == null) { throw new IllegalArgumentException("Cannot
		 * set Spell Base Stat to null"); }
		 */
		if (baseStat != null && baseStat.trim().length() == 0) {
			throw new IllegalArgumentException(
					"Cannot set Spell Base Stat to an empty String.");
		}
		spellBaseStatAbbr = baseStat == null ? null : baseStat.trim();
	}

	/**
	 * Returns the abbreviation of the Base Spell Stat for this SpellProgression
	 * 
	 * @return The abbreviation of the Base Spell Stat for this SpellProgression
	 */
	public String getSpellBaseStatAbbr() {
		return spellBaseStatAbbr;
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
	 * Returns the minimum class level required to acquire a spell of the given
	 * spell level (and potential application of bonuses).
	 * 
	 * There are two situations that can also be distinguished:
	 * 
	 * If allowBonus is true, then this will return a class level where the CAST
	 * or KNOWN spell count is equal to zero (since it assumes that bonuses
	 * could provide that spell level). This DOES NOT return a class level where
	 * a spell count is "-" (or spells are not available at all).
	 * 
	 * If allowBonus is false, this this will return a class level where the
	 * CAST or KNOWN spell count is greater than zero (since no bonuses would be
	 * applied to make the spell count a non-zero value)
	 * 
	 * *WARNING* This method is KNOWN to be broken if this SpellProgressionInfo
	 * contains a spell formula. Analysis must be done in context to the formula
	 * in that case.
	 * 
	 * @param spellLevel
	 *            The spell level for which the minimum required class level
	 *            will be returned
	 * @param allowBonus
	 *            true if the class level is allowed to have a "0" CAST or KNOWN
	 *            spell count.
	 * @return The minimum class level required to acquire a spell of the given
	 *         spell level (and potential application of bonuses)
	 */
	public int getMinLevelForSpellLevel(int spellLevel, boolean allowBonus) {
		if (castProgression != null) {
			int lvl = castProgression.getMinLevelForSpellLevel(spellLevel,
					allowBonus);
			if (lvl != -1) {
				return lvl;
			}
		}
		/*
		 * CONSIDER Should the castMap really dominate the knownMap like this??? -
		 * I know that it probably returns the intended result, but it needs to
		 * be explained better in the comments. - thpr 11/9/06
		 */
		if (knownProgression != null) {
			int lvl = knownProgression.getMinLevelForSpellLevel(spellLevel,
					allowBonus);
			if (lvl != -1) {
				return lvl;
			}
		}

		return -1;
	}

	/**
	 * Returns the theoretical maximum spell level for the given class level.
	 * 
	 * Note that this is a theoretical highest level, and based on the abilities
	 * of a PlayerCharacter, the highest spell level may not be available to
	 * that PlayerCharacter.
	 * 
	 * This theoretical test WILL return a spell level where the class grants 0
	 * known or cast spells, as bonuses acquired from Stats can provide use of
	 * that spell level.
	 * 
	 * @param classLevel
	 *            The class level for which the theoretical maximum spell level
	 *            should be returned.
	 * @return The theoretical maximum spell level for the given class level; -1
	 *         if this SpellProgressionInfo does not contain any KNOWN or CAST
	 *         spell progressions for the given class level.
	 */
	public int getMaxSpellLevelForClassLevel(int classLevel) {
		/*
		 * Delegation to get*ForLevel is required because it is possible that
		 * the given class level itself does not have a CAST or KNOWN
		 * progression, but that a lower level does. Those methods account for
		 * that situation.
		 */
		if (castProgression != null) {
			List<Formula> knownList = castProgression
					.getProgressionForLevel(classLevel);
			if (knownList != null) {
				return knownList.size() - 1;
			}
		}
		if (knownProgression != null) {
			List<Formula> knownList = knownProgression
					.getProgressionForLevel(classLevel);
			if (knownList != null) {
				return knownList.size() - 1;
			}
		}
		return -1;
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
		SpellProgressionInfo spi = (SpellProgressionInfo) super.clone();
		/*
		 * Each of knownMap, specialtyKnownMap, and castMap need one level deep
		 * clones. Since the Lists that are stored are never individually
		 * modified in SpellProgressionInfo (they are always overwritten by a
		 * new set*) there is no need to do a full depth clone. However, the one
		 * level deep clone is required in case there is a PCClass.COPY or
		 * something that then mods a KNOWN or CAST.
		 */
		if (knownProgression != null) {
			spi.knownProgression = knownProgression.clone();
		}
		if (specialtyKnownProgression != null) {
			spi.specialtyKnownProgression = specialtyKnownProgression.clone();
		}
		if (castProgression != null) {
			spi.castProgression = castProgression.clone();
		}
		return spi;
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

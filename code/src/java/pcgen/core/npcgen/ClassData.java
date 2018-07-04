/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
package pcgen.core.npcgen;

import java.util.HashMap;
import java.util.Map;

import pcgen.base.util.WeightedCollection;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.SubClass;
import pcgen.core.spell.Spell;

/**
 * Stores information about how to randomly generate selections for a class.
 * 
 * 
 */
public class ClassData
{
	private PCClass theClass = null;

	// TODO Can this be a PCStat?
	private WeightedCollection<PCStat> theStatWeights = null;
	private WeightedCollection<SkillChoice> theSkillWeights = null;
	private Map<AbilityCategory, WeightedCollection<Ability>> theAbilityWeights = null;
	private WeightedCollection<Deity> theDeityWeights = null;
	private Map<String, WeightedCollection<Domain>> theDomainWeights = null;
	private Map<Integer, WeightedCollection<Spell>> theKnownSpellWeights = null;
	private Map<Integer, WeightedCollection<Spell>> thePreparedSpellWeights = null;
	private WeightedCollection<String> theSubClassWeights = null;

	/**
	 * Creates an empty <tt>ClassData</tt> object
	 * 
	 * @param aClass The key of the class this data is for
	 */
	public ClassData(final PCClass aClass)
	{
		theClass = aClass;
	}

	public PCClass getPCClass()
	{
		return theClass;
	}

	/**
	 * @param stat The stat to add
	 * @param aWeight The weight to associate with it.
	 */
	public void addStat(final PCStat stat, final int aWeight)
	{
		if (theStatWeights == null)
		{
			theStatWeights = new WeightedCollection<>();
		}
		theStatWeights.add(stat, aWeight);
	}

	/**
	 * @return <tt>WeightedCollection</tt> of stat abbreviations.
	 */
	public WeightedCollection<PCStat> getStatWeights()
	{
		// Make sure that we have all the stats
		for (final PCStat stat : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(PCStat.class))
		{
			if (theStatWeights == null || !theStatWeights.contains(stat))
			{
				addStat(stat, 1);
			}
		}
		return theStatWeights;
	}

	/**
	 * @param aKey
	 * @param aWeight
	 */
	public void addSkill(final String aKey, final int aWeight)
	{
		if (theSkillWeights == null)
		{
			theSkillWeights = new WeightedCollection<>();
		}
		for (final SkillChoice sc : theSkillWeights)
		{
			if (sc.hasSkill(aKey))
			{
				return;
			}
		}
		theSkillWeights.add(new SkillChoice(aKey), aWeight);
	}

	/**
	 * @param aKey
	 */
	public void removeSkill(final String aKey)
	{
		if (theSkillWeights == null)
		{
			return;
		}
		theSkillWeights.remove(new SkillChoice(aKey));
	}

	/**
	 * @return <tt>WeightedCollection</tt> of Skill keys
	 */
	public WeightedCollection<SkillChoice> getSkillWeights()
	{
		return theSkillWeights;
	}

	/**
	 * @param aCategory
	 * @param anAbility
	 * @param aWeight
	 */
	public void addAbility(final AbilityCategory aCategory, final Ability anAbility, final int aWeight)
	{
		if (theAbilityWeights == null)
		{
			theAbilityWeights = new HashMap<>();
		}
		WeightedCollection<Ability> abilities = theAbilityWeights.get(aCategory);
		if (abilities == null)
		{
			abilities = new WeightedCollection<>();
			theAbilityWeights.put(aCategory, abilities);
		}
		if (!abilities.contains(anAbility))
		{
			abilities.add(anAbility, aWeight);
		}
	}

	/**
	 * Removes an Ability from the list of abilities.
	 * 
	 * @param aCategory The AbilityCategory to remove the ability for
	 * @param anAbility The Ability to remove
	 */
	public void removeAbility(final AbilityCategory aCategory, final Ability anAbility)
	{
		if (theAbilityWeights == null)
		{
			return;
		}
		final WeightedCollection<Ability> abilities = theAbilityWeights.get(aCategory);
		if (abilities == null)
		{
			return;
		}
		abilities.remove(anAbility);
	}

	/**
	 * Gets the Abilities of the specified category.
	 * 
	 * <p>If there is no data for this category, all Abilities for the category
	 * will be added to the list with the same weight.
	 * 
	 * @param aCategory The category of ability to retrieve
	 * @return A <tt>WeightedCollection</tt> of Ability objects
	 */
	public WeightedCollection<Ability> getAbilityWeights(final AbilityCategory aCategory)
	{
		if (theAbilityWeights == null)
		{
			return null;
		}
		return theAbilityWeights.get(aCategory);
	}

	public void addDeity(final Deity aDeity, final int aWeight)
	{
		if (theDeityWeights == null)
		{
			theDeityWeights = new WeightedCollection<>();
		}

		theDeityWeights.add(aDeity, aWeight);
	}

	public WeightedCollection<Deity> getDeityWeights()
	{
		if (theDeityWeights == null)
		{
			for (final Deity deity : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Deity.class))
			{
				addDeity(deity, 1);
			}
		}
		return theDeityWeights;
	}

	public void addDomain(final String aDeityKey, final Domain aDomain, final int aWeight)
	{
		if (theDomainWeights == null)
		{
			theDomainWeights = new HashMap<>();
		}
		WeightedCollection<Domain> domains = theDomainWeights.get(aDeityKey);
		if (domains == null)
		{
			domains = new WeightedCollection<>();
			theDomainWeights.put(aDeityKey, domains);
		}
		domains.add(aDomain, aWeight);
	}

	public WeightedCollection<Domain> getDomainWeights(final String aDeityKey)
	{
		if (theDomainWeights == null)
		{
			theDomainWeights = new HashMap<>();
		}
		WeightedCollection<Domain> domains = theDomainWeights.get(aDeityKey);
		if (domains == null)
		{
			domains = new WeightedCollection<>();
			Deity deity =
					Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Deity.class, aDeityKey);
			for (CDOMReference<Domain> deityDomains : deity.getSafeListMods(Deity.DOMAINLIST))
			{
				domains.addAll(deityDomains.getContainedObjects(),
					deity.getListAssociations(Deity.DOMAINLIST, deityDomains).size());
			}
		}
		return domains;
	}

	public void addKnownSpell(final int aLevel, final Spell aSpell, final int aWeight)
	{
		if (theKnownSpellWeights == null)
		{
			theKnownSpellWeights = new HashMap<>();
		}
		WeightedCollection<Spell> spells = theKnownSpellWeights.get(aLevel);
		if (spells == null)
		{
			spells = new WeightedCollection<>();
			theKnownSpellWeights.put(aLevel, spells);
		}
		if (!spells.contains(aSpell))
		{
			spells.add(aSpell, aWeight);
		}
	}

	public void removeKnownSpell(final int aLevel, final Spell aSpell)
	{
		if (theKnownSpellWeights == null)
		{
			return;
		}

		final WeightedCollection<Spell> spells = theKnownSpellWeights.get(aLevel);
		if (spells != null)
		{
			spells.remove(aSpell);
		}
	}

	public WeightedCollection<Spell> getKnownSpellWeights(final int aLevel, PlayerCharacter pc)
	{
		if (theKnownSpellWeights == null)
		{
			theKnownSpellWeights = new HashMap<>();
		}
		WeightedCollection<Spell> spells = theKnownSpellWeights.get(aLevel);
		if (spells == null)
		{
			spells = new WeightedCollection<>();

			for (final Spell spell : pc.getSpellsIn(theClass.get(ObjectKey.CLASS_SPELLLIST), aLevel))
			{
				spells.add(spell, 1);
			}
		}
		return spells;
	}

	public void addPreparedSpell(final int aLevel, final Spell aSpell, final int aWeight)
	{
		if (thePreparedSpellWeights == null)
		{
			thePreparedSpellWeights = new HashMap<>();
		}
		WeightedCollection<Spell> spells = thePreparedSpellWeights.get(aLevel);
		if (spells == null)
		{
			spells = new WeightedCollection<>();
			thePreparedSpellWeights.put(aLevel, spells);
		}
		if (!spells.contains(aSpell))
		{
			spells.add(aSpell, aWeight);
		}
	}

	public void removePreparedSpell(final int aLevel, final Spell aSpell)
	{
		if (thePreparedSpellWeights == null)
		{
			return;
		}

		final WeightedCollection<Spell> spells = thePreparedSpellWeights.get(aLevel);
		if (spells != null)
		{
			spells.remove(aSpell);
		}
	}

	public WeightedCollection<Spell> getPreparedSpellWeights(final int aLevel, PlayerCharacter pc)
	{
		if (thePreparedSpellWeights == null)
		{
			thePreparedSpellWeights = new HashMap<>();
		}
		WeightedCollection<Spell> spells = thePreparedSpellWeights.get(aLevel);
		if (spells == null)
		{
			spells = new WeightedCollection<>();

			for (final Spell spell : pc.getSpellsIn(theClass.get(ObjectKey.CLASS_SPELLLIST), aLevel))
			{
				spells.add(spell, 1);
			}
		}
		return spells;
	}

	public void addSubClass(final String aKey, final int aWeight)
	{
		if (theSubClassWeights == null)
		{
			theSubClassWeights = new WeightedCollection<>();
		}
		theSubClassWeights.add(aKey, aWeight);
	}

	public WeightedCollection<String> getSubClassWeights()
	{
		if (theSubClassWeights == null)
		{
			if (theClass != null)
			{
				for (final SubClass subClass : theClass.getListFor(ListKey.SUB_CLASS))
				{
					addSubClass(subClass.getKeyName(), 1);
				}
			}
		}
		return theSubClassWeights;
	}
}

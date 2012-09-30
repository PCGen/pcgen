/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from Domain.java and PCClass.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.analysis;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.ClassSource;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.list.DomainSpellList;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.QualifiedObject;
import pcgen.core.character.CharacterSpell;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.spell.Spell;

public class DomainApplication
{
	/**
	 * Sets the locked flag on a PC
	 * 
	 * @param pc
	 */
	public static void applyDomain(PlayerCharacter pc, Domain d)
	{
		ClassSource source = pc.getDomainSource(d);
		PCClass aClass = pc.getClassKeyed(source.getPcclass().getKeyName());

		if (aClass != null)
		{
			int maxLevel;

			for (maxLevel = 0; maxLevel < 10; maxLevel++)
			{
				if (pc.getSpellSupport(aClass).getCastForLevel(maxLevel, pc) == 0)
				{
					break;
				}
			}

			if (maxLevel > 0)
			{
				addSpellsToClassForLevels(pc, d, aClass, 0, maxLevel - 1);
			}

			if ((maxLevel > 1)
					&& (aClass.getSafe(IntegerKey.KNOWN_SPELLS_FROM_SPECIALTY) == 0))
			{
				DomainSpellList domainSpellList = d
						.get(ObjectKey.DOMAIN_SPELLLIST);
				final List<Spell> aList = Globals.getSpellsIn(-1, Collections
						.singletonList(domainSpellList), pc);

				for (Spell gcs : aList)
				{
					if (SpellLevel.getFirstLvlForKey(gcs, domainSpellList, pc) < maxLevel)
					{
						pc.setAssoc(aClass, AssociationKey.DOMAIN_SPELL_COUNT,
								1);
						break;
					}
				}
			}
		}

		Collection<CDOMReference<Spell>> mods = d.getSafeListMods(Spell.SPELLS);
		for (CDOMReference<Spell> ref : mods)
		{
			Collection<Spell> spells = ref.getContainedObjects();
			Collection<AssociatedPrereqObject> assoc = d.getListAssociations(Spell.SPELLS, ref);
			for (AssociatedPrereqObject apo : assoc)
			{
				if (!PrereqHandler.passesAll(apo.getPrerequisiteList(), pc, d))
				{
					continue;
				}
				for (Spell s : spells)
				{
					String book = apo.getAssociation(AssociationKey.SPELLBOOK);
					List<CharacterSpell> aList = pc
							.getCharacterSpells(aClass, s, book, -1);

					if (aList.isEmpty())
					{
						Formula times = apo
								.getAssociation(AssociationKey.TIMES_PER_UNIT);
						CharacterSpell cs = new CharacterSpell(d, s);
						int resolvedTimes = times.resolve(pc,
								d.getQualifiedKey()).intValue();
						cs.addInfo(1, resolvedTimes, book);
						pc.addCharacterSpell(aClass, cs);
					}
				}
			}
		}

		if (!pc.isImporting())
		{
			AddObjectActions.globalChecks(d, pc);
			BonusActivation.activateBonuses(d, pc);
		}
	}

	/**
	 * Remove a domain from the character.
	 * @param pc The character
	 * @param domain The domain.
	 */
	public static void removeDomain(PlayerCharacter pc, Domain domain)
	{
		ClassSource source = pc.getDomainSource(domain);
		PCClass aClass = pc.getClassKeyed(source.getPcclass().getKeyName());

		if (aClass != null)
		{
			int maxLevel;

			for (maxLevel = 0; maxLevel < 10; maxLevel++)
			{
				if (pc.getSpellSupport(aClass).getCastForLevel(maxLevel, pc) == 0)
				{
					break;
				}
			}

			if (maxLevel > 0)
			{
				removeSpellsFromClassForLevels(pc, domain, aClass);
			}

			if ((maxLevel > 1)
					&& (aClass.getSafe(IntegerKey.KNOWN_SPELLS_FROM_SPECIALTY) == 0))
			{
				DomainSpellList domainSpellList = domain
						.get(ObjectKey.DOMAIN_SPELLLIST);
				final List<Spell> aList = Globals.getSpellsIn(-1, Collections
						.singletonList(domainSpellList), pc);

				for (Spell gcs : aList)
				{
					if (SpellLevel.getFirstLvlForKey(gcs, domainSpellList, pc) < maxLevel)
					{
						pc.removeAssoc(aClass, AssociationKey.DOMAIN_SPELL_COUNT);
						break;
					}
				}
			}
		}

		if (!pc.isImporting())
		{
			AddObjectActions.globalChecks(domain, pc);
			BonusActivation.activateBonuses(domain, pc);
		}
	}
	
	/**
	 * Remove any spells granted by the domain to the class.
	 * @param pc The character.
	 * @param domain The domain.
	 * @param aClass The class which would have the spells allocated.
	 */
	public static void removeSpellsFromClassForLevels(PlayerCharacter pc, Domain domain,
			PCClass aClass)
	{
		if (aClass == null)
		{
			return;
		}

		Collection<? extends CharacterSpell> characterSpells = pc.getCharacterSpells(aClass);
		for (CharacterSpell characterSpell : characterSpells)
		{
			if (characterSpell.getOwner() == domain)
			{
				pc.removeCharacterSpell(aClass, characterSpell);
			}
		}
	}
	
	public static void addSpellsToClassForLevels(PlayerCharacter pc, Domain d,
			PCClass aClass, int minLevel, int maxLevel)
	{
		if (aClass == null)
		{
			return;
		}

		for (int aLevel = minLevel; aLevel <= maxLevel; aLevel++)
		{
			List<Spell> domainSpells = Globals.getSpellsIn(aLevel, Collections
					.singletonList(d.get(ObjectKey.DOMAIN_SPELLLIST)), pc);

			for (Spell spell : domainSpells)
			{
				List<CharacterSpell> slist =
						pc.getCharacterSpells(aClass, spell, Globals
							.getDefaultSpellBook(), aLevel);
				boolean flag = true;

				for (CharacterSpell cs1 : slist)
				{
					flag = !(cs1.getOwner().equals(d));

					if (!flag)
					{
						break;
					}
				}

				if (flag)
				{
					CharacterSpell cs = new CharacterSpell(d, spell);
					cs.addInfo(aLevel, 1, Globals.getDefaultSpellBook());
					pc.addCharacterSpell(aClass, cs);
				}
			}
		}
	}

	private static void addDomain(final PlayerCharacter aPC, PCClass cl, Domain d,
			final boolean adding)
	{
		if (d.qualifies(aPC, d))
		{
			if (adding)
			{
				ClassSource source = aPC.getDomainSource(d);
				if (source != null
						&& !cl.getKeyName().equals(
								source.getPcclass().getKeyName()))
				{
					// TODO Not entirely correct, as this takes this level, not
					// the level where BONUS DOMAINS was present
					ClassSource cs = new ClassSource(cl, aPC
							.getLevel(cl));
					aPC.addDomain(d, cs);
					applyDomain(aPC, d);
				}
			}
			else
			{
				if (aPC.hasDomain(d))
				{
					aPC.removeDomain(d);
				}
			}
		}
	}

	public static void modDomainsForLevel(PCClass cl, final int aLevel, final boolean adding,
		final PlayerCharacter aPC)
	{
	
		// any domains set by level would have already been saved
		// and don't need to be re-set at level up time
		if (aPC.isImporting())
		{
			return;
		}
	
		/*
		 * Note this uses ALL of the domains up to and including this level,
		 * because there is the possibility (albeit strange) that the PC was not
		 * qualified at a previous level change, but the PlayerCharacter is now
		 * qualified for the given Domain. Even this has quirks, since it is
		 * only applied at the time of level increase, but I think that quirk
		 * should be resolved by a CDOM system around 6.0 - thpr 10/23/06
		 */
		for (QualifiedObject<CDOMSingleRef<Domain>> qo : cl.getSafeListFor(ListKey.DOMAIN))
		{
			CDOMSingleRef<Domain> ref = qo.getObject(aPC, cl);
			if (ref != null)
			{
				addDomain(aPC, cl, ref.resolvesTo(), adding);
			}
		}
		for (int i = 0 ; i <= aLevel; i++)
		{
			// TODO This stinks for really high level characters - can this ever
			// get null back?
			PCClassLevel pcl = aPC.getActiveClassLevel(cl, i);
			for (QualifiedObject<CDOMSingleRef<Domain>> qo : pcl
					.getSafeListFor(ListKey.DOMAIN))
			{
				CDOMSingleRef<Domain> ref = qo.getObject(aPC, cl);
				if (ref != null)
				{
					addDomain(aPC, cl, ref.resolvesTo(), adding);
				}
			}
		}
	}

}

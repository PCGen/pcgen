/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from Domain.java
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.CharacterDomain;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.CharacterSpell;
import pcgen.core.chooser.ChooserUtilities;
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
		String keyName = d.getKeyName();
		final CharacterDomain aCD = pc.getCharacterDomainForDomain(keyName);
		PCClass aClass = null;

		if (aCD != null)
		{
			if (aCD.isFromPCClass())
			{
				aClass = pc.getClassKeyed(aCD.getObjectName());

				if (aClass != null)
				{
					int maxLevel;

					for (maxLevel = 0; maxLevel < 10; maxLevel++)
					{
						if (aClass.getCastForLevel(maxLevel, pc) == 0)
						{
							break;
						}
					}

					if (maxLevel > 0)
					{
						addSpellsToClassForLevels(pc, d, aClass, 0, maxLevel - 1);
					}

					if ((maxLevel > 1)
							&& (aClass
									.getSafe(IntegerKey.KNOWN_SPELLS_FROM_SPECIALTY) == 0))
					{
						DomainSpellList domainSpellList = d
								.get(ObjectKey.DOMAIN_SPELLLIST);
						final List<Spell> aList = Globals.getSpellsIn(-1,
								Collections.singletonList(domainSpellList));

						for (Spell gcs : aList)
						{
							if (SpellLevel
									.getFirstLvlForKey(gcs, domainSpellList, pc) < maxLevel)
							{
								pc.setAssoc(aClass,
										AssociationKey.DOMAIN_SPELL_COUNT, 1);
								break;
							}
						}
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
						pc.addAssoc(aClass, AssociationListKey.CHARACTER_SPELLS, cs);
					}
				}
			}
		}

		// sage_sam stop here
		String choiceString = d.getSafe(StringKey.CHOICE_STRING);

		if ((choiceString.length() > 0) && !pc.isImporting()
				&& !choiceString.startsWith("FEAT|"))
		{
			ChooserUtilities.modChoices(d, new ArrayList<Object>(),
					new ArrayList<Object>(), true, pc, true, null);
		}

		if (!pc.isImporting())
		{
			d.globalChecks(pc);
			d.activateBonuses(pc);
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
					.singletonList(d.get(ObjectKey.DOMAIN_SPELLLIST)));

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
					pc.addAssoc(aClass, AssociationListKey.CHARACTER_SPELLS, cs);
				}
			}
		}
	}

}

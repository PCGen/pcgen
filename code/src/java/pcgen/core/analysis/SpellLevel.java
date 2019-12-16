/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
package pcgen.core.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.spell.Spell;

public final class SpellLevel
{

	private SpellLevel()
	{
	}

	public static boolean levelForKeyContains(Spell sp, List<? extends CDOMList<Spell>> lists, int levelMatch,
		PlayerCharacter aPC)
	{
		if (lists == null || aPC == null)
		{
			return false;
		}
		Set<Integer> resultList = new TreeSet<>();
		HashMapToList<CDOMList<Spell>, Integer> pcli = aPC.getSpellLevelInfo(sp);
		for (CDOMList<Spell> spellList : lists)
		{
			List<Integer> levels = pcli.getListFor(spellList);
			if (levels != null)
			{
				resultList.addAll(levels);
			}
		}
		return levelMatch == -1 && !resultList.isEmpty() || levelMatch >= 0 && resultList.contains(levelMatch);
	}

	public static Integer[] levelForKey(Spell sp, List<? extends CDOMList<Spell>> lists, PlayerCharacter aPC)
	{
		List<Integer> list = new ArrayList<>();

		if (lists != null)
		{
			for (CDOMList<Spell> spellList : lists)
			{
				list.add(getFirstLvlForKey(sp, spellList, aPC));
			}
		}

		return list.toArray(new Integer[0]);
	}

	/**
	 * Retrieve the first level that the pc receives the spell from the specified list. Note that this only returns 
	 * spells that the pc has available.
	 * 
	 * @param sp The spell to be found.
	 * @param list The spell list (e.g. a class spell list)
	 * @param aPC The character who must already have the spell.
	 * @return The level of the spell, or -1 if not found.
	 */
	public static int getFirstLvlForKey(Spell sp, CDOMList<Spell> list, PlayerCharacter aPC)
	{
		HashMapToList<CDOMList<Spell>, Integer> wLevelInfo = aPC.getSpellLevelInfo(sp);
		if ((wLevelInfo != null) && (!wLevelInfo.isEmpty()))
		{
			List<Integer> levelList = wLevelInfo.getListFor(list);
			if (levelList != null)
			{
				// In specific situations, this list may not be sorted, so we won't assume it is
				return Collections.min(levelList);
			}
		}
		return -1;
	}

	/**
	 * isLevel(int aLevel)
	 *
	 * @param aLevel
	 *            level of the spell
	 * @param aPC
	 * @return true if the spell is of the given level in any spell list
	 */
	public static boolean isLevel(Spell sp, int aLevel, PlayerCharacter aPC)
	{
		Integer levelKey = aLevel;
		MasterListInterface masterLists = SettingsHandler.getGame().getMasterLists();
		for (PCClass pcc : aPC.getClassSet())
		{
			ClassSpellList csl = pcc.get(ObjectKey.CLASS_SPELLLIST);
			Collection<AssociatedPrereqObject> assoc = masterLists.getAssociations(csl, sp);
			if (assoc != null)
			{
				for (AssociatedPrereqObject apo : assoc)
				{
					if (PrereqHandler.passesAll(apo, aPC, sp))
					{
						if (levelKey.equals(apo.getAssociation(AssociationKey.SPELL_LEVEL)))
						{
							return true;
						}
					}
				}
			}
		}
		for (Domain domain : aPC.getDomainSet())
		{
			DomainSpellList dsl = domain.get(ObjectKey.DOMAIN_SPELLLIST);
			Collection<AssociatedPrereqObject> assoc = masterLists.getAssociations(dsl, sp);
			if (assoc != null)
			{
				for (AssociatedPrereqObject apo : assoc)
				{
					if (PrereqHandler.passesAll(apo, aPC, sp))
					{
						if (levelKey.equals(apo.getAssociation(AssociationKey.SPELL_LEVEL)))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static int getFirstLevelForKey(Spell sp, List<? extends CDOMList<Spell>> lists, PlayerCharacter aPC)
	{
		Integer[] levelInt = levelForKey(sp, lists, aPC);
		int result = -1;

		if (levelInt.length > 0)
		{
            for (Integer integer : levelInt)
            {
                if (integer > -1)
                {
                    return integer;
                }
            }
		}

		return result;
	}
}

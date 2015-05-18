/*
 * SpellCountCalc
 * Copyright 2009 (c) Tom Parker <thpr@users.sourceforge.net>
 * derived from PCClass.java
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
package pcgen.core.analysis;

import java.util.List;

import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.identifier.SpellSchool;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SpellProhibitor;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;

public class SpellCountCalc
{

	public static int memorizedSpellForLevelBook(PlayerCharacter pc, PCClass cl, int aLevel,
			String bookName)
	{
		int m = 0;
		final List<CharacterSpell> aList =
				pc.getCharacterSpells(cl, null, bookName, aLevel);
	
		if (aList.isEmpty())
		{
			return m;
		}
	
		for (CharacterSpell cs : aList)
		{
			m += cs.getSpellInfoFor(bookName, aLevel).getTimes();
		}
	
		return m;
	}

	public static int memorizedSpecialtiesForLevelBook(int aLevel, String bookName,
			PlayerCharacter pc, PCClass cl)
	{
		int m = 0;
		final List<CharacterSpell> aList =
				pc.getCharacterSpells(cl, null, bookName, aLevel);
	
		if (aList.isEmpty())
		{
			return m;
		}
	
		for (CharacterSpell cs : aList)
		{
			if (cs.isSpecialtySpell(pc))
			{
				m += cs.getSpellInfoFor(bookName, aLevel).getTimes();
			}
		}
	
		return m;
	}

	public static boolean isSpecialtySpell(PlayerCharacter pc, PCClass cl, Spell aSpell)
	{
		String specialty = pc.getAssoc(cl, AssociationKey.SPECIALTY);
		if (specialty != null)
		{
			SpellSchool ss =
				Globals.getContext().getReferenceContext()
					.silentlyGetConstructedCDOMObject(
						SpellSchool.class, specialty);
			return (ss != null) && aSpell.containsInList(ListKey.SPELL_SCHOOL, ss)
					|| aSpell
							.containsInList(ListKey.SPELL_SUBSCHOOL, specialty)
					|| aSpell.containsInList(ListKey.SPELL_DESCRIPTOR,
							specialty);
		}
		return false;
	}

	public static boolean isProhibited(Spell aSpell, PCClass cl, PlayerCharacter aPC)
	{
		if (!aSpell.qualifies(aPC, aSpell))
		{
			return true;
		}
	
		for (SpellProhibitor prohibit : aPC.getProhibitedSchools(cl))
		{
			if (prohibit.isProhibited(aSpell, aPC, cl))
			{
				return true;
			}
		}
	
		return false;
	}

}

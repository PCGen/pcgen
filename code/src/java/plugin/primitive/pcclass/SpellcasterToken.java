/*
 * Copyright 2010 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.primitive.pcclass;

import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

public class SpellcasterToken implements PrimitiveToken<PCClass>
{

	private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;

	public boolean initialize(LoadContext context, Class<PCClass> cl,
			String value, String args)
	{
		if (value != null || args != null)
		{
			return false;
		}
		return true;
	}

	public String getTokenName()
	{
		return "SPELLCASTER";
	}

	public Class<PCClass> getReferenceClass()
	{
		return PCCLASS_CLASS;
	}

	public String getLSTformat()
	{
		return "SPELLCASTER";
	}

	public boolean allow(PlayerCharacter pc, PCClass pcc)
	{
		return isSpellCaster(pcc);
	}

	private boolean isSpellCaster(PCClass pcc)
	{
		return pcc.getSafe(ObjectKey.USE_SPELL_SPELL_STAT)
				|| pcc.getSafe(ObjectKey.CASTER_WITHOUT_SPELL_STAT)
				|| (pcc.get(ObjectKey.SPELL_STAT) != null);
	}

	public Set<PCClass> getSet(PlayerCharacter pc)
	{
		HashSet<PCClass> classSet = new HashSet<PCClass>();
		for (PCClass pcc : Globals.getContext().ref
				.getConstructedCDOMObjects(PCCLASS_CLASS))
		{
			if (isSpellCaster(pcc))
			{
				classSet.add(pcc);
			}
		}
		return classSet;
	}

	public GroupingState getGroupingState()
	{
		return GroupingState.ANY;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof SpellcasterToken;
	}

	@Override
	public int hashCode()
	{
		return 123023;
	}
}

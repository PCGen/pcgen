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
package plugin.primitive.spell;

import java.util.Collection;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

/**
 * SubSchoolToken is a Primitive that represents the SubSchools that are on a Spell.
 */
public class SubSchoolToken implements PrimitiveToken<Spell>, PrimitiveFilter<Spell>
{
	private static final Class<Spell> SPELL_CLASS = Spell.class;
	private String subschool;
	private CDOMReference<Spell> allSpells;

	@Override
	public boolean initialize(LoadContext context, Class<Spell> cl, String value, String args)
	{
		if (args != null)
		{
			return false;
		}
		subschool = value;
		allSpells = context.getReferenceContext().getCDOMAllReference(SPELL_CLASS);
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "SUBSCHOOL";
	}

	@Override
	public Class<Spell> getReferenceClass()
	{
		return SPELL_CLASS;
	}

	@Override
	public String getLSTformat(boolean useAny)
	{
		return getTokenName() + '=' + subschool;
	}

	@Override
	public boolean allow(PlayerCharacter pc, Spell spell)
	{
		return spell.containsInList(ListKey.SPELL_SUBSCHOOL, subschool);
	}

	@Override
	public GroupingState getGroupingState()
	{
		return GroupingState.ANY;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof SubSchoolToken other)
		{
			if (subschool == null)
			{
				return other.subschool == null;
			}
			return subschool.equals(other.subschool);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return subschool == null ? -7 : subschool.hashCode();
	}

	@Override
	public <R> Collection<? extends R> getCollection(PlayerCharacter pc, Converter<Spell, R> c)
	{
		return c.convert(allSpells, this);
	}

}

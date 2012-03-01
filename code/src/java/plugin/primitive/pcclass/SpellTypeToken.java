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

import java.util.Collection;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

public class SpellTypeToken implements PrimitiveToken<PCClass>, PrimitiveFilter<PCClass>
{
	private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;
	private String spelltype;
	private CDOMReference<PCClass> allClasses;

	@Override
	public boolean initialize(LoadContext context, Class<PCClass> cl,
		String value, String args)
	{
		if (args != null)
		{
			return false;
		}
		spelltype = value;
		allClasses = context.ref.getCDOMAllReference(PCCLASS_CLASS);
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "SPELLTYPE";
	}

	@Override
	public Class<PCClass> getReferenceClass()
	{
		return PCCLASS_CLASS;
	}

	@Override
	public String getLSTformat(boolean useAny)
	{
		return getTokenName() + "=" + spelltype;
	}

	@Override
	public boolean allow(PlayerCharacter pc, PCClass cl)
	{
		return spelltype.equals(cl.get(StringKey.SPELLTYPE));
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
		if (obj instanceof SpellTypeToken)
		{
			SpellTypeToken other = (SpellTypeToken) obj;
			return spelltype.equals(other.spelltype);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return spelltype == null ? -7 : spelltype.hashCode();
	}

	@Override
	public <R> Collection<R> getCollection(PlayerCharacter pc,
			Converter<PCClass, R> c)
	{
		return c.convert(allClasses, this);
	}
}

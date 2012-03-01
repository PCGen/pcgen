/*
 * Copyright 2009 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.primitive.deity;

import java.util.Collection;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Pantheon;
import pcgen.core.Deity;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

public class PantheonToken implements PrimitiveToken<Deity>, PrimitiveFilter<Deity>
{

	private static final Class<Deity> DEITY_CLASS = Deity.class;

	private Pantheon pantheon;
	private CDOMReference<Deity> allDeities;

	@Override
	public boolean initialize(LoadContext context, Class<Deity> cl,
			String value, String args)
	{
		if (args != null)
		{
			return false;
		}
		pantheon = Pantheon.getConstant(value);
		allDeities = context.ref.getCDOMAllReference(DEITY_CLASS);
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "PANTHEON";
	}

	@Override
	public Class<Deity> getReferenceClass()
	{
		return DEITY_CLASS;
	}

	@Override
	public String getLSTformat(boolean useAny)
	{
		return getTokenName() + "=" + pantheon.toString();
	}

	@Override
	public boolean allow(PlayerCharacter pc, Deity deity)
	{
		return deity.containsInList(ListKey.PANTHEON, pantheon);
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
		if (obj instanceof PantheonToken)
		{
			PantheonToken other = (PantheonToken) obj;
			return pantheon.equals(other.pantheon);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return pantheon == null ? -3 : pantheon.hashCode();
	}

	@Override
	public <R> Collection<R> getCollection(PlayerCharacter pc, Converter<Deity, R> c)
	{
		return c.convert(allDeities, this);
	}
}

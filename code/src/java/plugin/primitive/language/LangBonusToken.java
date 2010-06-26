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
package plugin.primitive.language;

import java.util.Set;

import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.Language;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

public class LangBonusToken implements PrimitiveToken<Language>
{

	private static final Class<Language> LANGUAGE_CLASS = Language.class;

	public boolean initialize(LoadContext context, Class<Language> cl,
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
		return "LANGBONUS";
	}

	public Class<Language> getReferenceClass()
	{
		return LANGUAGE_CLASS;
	}

	public String getLSTformat()
	{
		return getTokenName();
	}

	public boolean allow(PlayerCharacter pc, Language l)
	{
		return pc.getLanguageBonusSelectionList().contains(l);
	}

	public Set<Language> getSet(PlayerCharacter pc)
	{
		return pc.getLanguageBonusSelectionList();
	}

	public GroupingState getGroupingState()
	{
		return GroupingState.ANY;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof LangBonusToken;
	}

	@Override
	public int hashCode()
	{
		return 3568;
	}
}

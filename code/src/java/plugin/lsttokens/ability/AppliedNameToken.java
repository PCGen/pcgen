/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.ability;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Ability;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with APPLIEDNAME Token
 */
public class AppliedNameToken extends AbstractNonEmptyToken<Ability>
		implements CDOMPrimaryToken<Ability>
{

	@Override
	public String getTokenName()
	{
		return "APPLIEDNAME";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, Ability ability,
		String value)
	{
		context.getObjectContext().put(ability, StringKey.APPLIED_NAME, value);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Ability ability)
	{
		String name = context.getObjectContext().getString(ability,
				StringKey.APPLIED_NAME);
		if (name == null)
		{
			return null;
		}
		return new String[] { name };
	}

	@Override
	public Class<Ability> getTokenClass()
	{
		return Ability.class;
	}
}

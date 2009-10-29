/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.equipment;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryParserToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with FUMBLERANGE token
 */
public class FumblerangeToken extends AbstractNonEmptyToken<Equipment> implements
		CDOMPrimaryParserToken<Equipment>
{

	@Override
	public String getTokenName()
	{
		return "FUMBLERANGE";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context,
		Equipment eq, String value)
	{
		context.getObjectContext().put(eq, StringKey.FUMBLE_RANGE,
				Constants.LST_DOT_CLEAR.equals(value) ? null : value);
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		String range = context.getObjectContext().getString(eq,
				StringKey.FUMBLE_RANGE);
		if (range == null)
		{
			return null;
		}
		return new String[] { range };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}

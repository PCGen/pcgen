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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.core.SizeAdjustment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with SIZE token 
 */
public class SizeToken implements CDOMPrimaryToken<Equipment>
{

	@Override
	public String getTokenName()
	{
		return "SIZE";
	}

	@Override
	public ParseResult parseToken(LoadContext context, Equipment eq, String value)
	{
		SizeAdjustment size =
				context.ref.getAbbreviatedObject(SizeAdjustment.class,
					value);
		if (size == null)
		{
			return new ParseResult.Fail("Unable to find Size: " + value);
		}
		context.getObjectContext().put(eq, ObjectKey.BASESIZE, size);
		context.getObjectContext().put(eq, ObjectKey.SIZE, size);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Equipment eq)
	{
		SizeAdjustment res = context.getObjectContext().getObject(eq,
				ObjectKey.BASESIZE);
		if (res == null)
		{
			return null;
		}
		return new String[]{res.getAbbreviation()};
	}

	@Override
	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}

}

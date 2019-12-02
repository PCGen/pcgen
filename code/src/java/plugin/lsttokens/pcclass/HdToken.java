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
package plugin.lsttokens.pcclass;

import pcgen.cdom.content.HitDie;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with HD Token
 */
public class HdToken implements CDOMPrimaryToken<PCClass>
{

	@Override
	public String getTokenName()
	{
		return "HD";
	}

	@Override
	public ParseResult parseToken(LoadContext context, PCClass pcc, String value)
	{
		try
		{
			Integer in = Integer.valueOf(value);
			if (in <= 0)
			{
				return new ParseResult.Fail(getTokenName() + " must be an integer > 0");
			}
			context.getObjectContext().put(pcc, ObjectKey.LEVEL_HITDIE, new HitDie(in));
			return ParseResult.SUCCESS;
		}
		catch (NumberFormatException nfe)
		{
			return new ParseResult.Fail(
				getTokenName() + " expected an integer.  Tag must be of the form: " + getTokenName() + ":<int>");
		}
	}

	@Override
	public String[] unparse(LoadContext context, PCClass pcc)
	{
		HitDie lpf = context.getObjectContext().getObject(pcc, ObjectKey.LEVEL_HITDIE);
		if (lpf == null)
		{
			return null;
		}
		if (lpf.getDie() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[]{Integer.toString(lpf.getDie())};
	}

	@Override
	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}

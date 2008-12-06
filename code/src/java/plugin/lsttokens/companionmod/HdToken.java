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
package plugin.lsttokens.companionmod;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.character.CompanionMod;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with HD Token
 */
public class HdToken extends AbstractToken implements
		CDOMPrimaryToken<CompanionMod>
{

	@Override
	public String getTokenName()
	{
		return "HD";
	}

	public boolean parse(LoadContext context, CompanionMod cMod, String value)
	{
		try
		{
			Integer in = Integer.valueOf(value);
			if (in.intValue() <= 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer > 0");
				return false;
			}
			context.getObjectContext().put(cMod, IntegerKey.HIT_DIE, in);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
				+ " expected an integer.  Tag must be of the form: "
				+ getTokenName() + ":<int>");
			return false;
		}
	}

	public String[] unparse(LoadContext context, CompanionMod cMod)
	{
		Integer hands =
				context.getObjectContext().getInteger(cMod, IntegerKey.HIT_DIE);
		if (hands == null)
		{
			return null;
		}
		if (hands.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[]{hands.toString()};
	}

	public Class<CompanionMod> getTokenClass()
	{
		return CompanionMod.class;
	}
}

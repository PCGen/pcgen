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

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * Class deals with XTRAFEATS Token
 */
public class XtrafeatsToken implements CDOMPrimaryToken<PCClass>
{

	/**
	 * Get Token name
	 * 
	 * @return token name
	 */
	@Override
	public String getTokenName()
	{
		return "XTRAFEATS";
	}

	@Override
	public ParseResult parseToken(LoadContext context, PCClass pcc, String value)
	{
		int featCount;
		try
		{
			featCount = Integer.parseInt(value);
			if (featCount == 0)
			{
				Logging.deprecationPrint(getTokenName() + " should not be used if zero (default is zero)", context);
			}
			else if (featCount <= 0)
			{
				return new ParseResult.Fail("Number in " + getTokenName() + " must be greater than zero: " + value);
			}
			context.getObjectContext().put(pcc, IntegerKey.START_FEATS, featCount);
			return ParseResult.SUCCESS;
		}
		catch (NumberFormatException nfe)
		{
			return new ParseResult.Fail("Invalid Number in " + getTokenName() + ": " + value);
		}
	}

	@Override
	public String[] unparse(LoadContext context, PCClass obj)
	{
		Integer feats = context.getObjectContext().getInteger(obj, IntegerKey.START_FEATS);
		if (feats == null)
		{
			return null;
		}
		if (feats <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[]{feats.toString()};
	}

	@Override
	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}

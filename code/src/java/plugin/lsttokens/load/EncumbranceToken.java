/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2006 (C) Devon Jones <soulcatcher@evilsoft.org>
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
package plugin.lsttokens.load;

import pcgen.core.system.LoadInfo;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * {@code EncumbranceToken}
 * 
 */
public class EncumbranceToken extends AbstractNonEmptyToken<LoadInfo>
		implements CDOMPrimaryToken<LoadInfo>, DeferredToken<LoadInfo>
{

	@Override
	public String getTokenName()
	{
		return "ENCUMBRANCE";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, LoadInfo info, String value)
	{
		String[] tokens = value.split("\\|");
		int tokenCount = tokens.length;
		if ((tokenCount != 2) && (tokenCount != 4))
		{
			return new ParseResult.Fail("Expected " + getTokenName() + " to have 2 or 4 arguments, found: " + value);
		}

		String multString = tokens[1];
		int divLoc = multString.indexOf('/');
		double mult;
		if (divLoc == -1)
		{
			try
			{
				mult = Double.parseDouble(multString);
			}
			catch (NumberFormatException e)
			{
				return new ParseResult.Fail(
					"Expected " + getTokenName() + " multiple to be a decimal (or a fraction), found: " + multString);
			}
		}
		else
		{
			if (divLoc != multString.lastIndexOf('/'))
			{
				return new ParseResult.Fail(
					"Expected " + getTokenName() + " multiple to be a decimal or a fraction, found: " + multString);
			}
			mult = Double.parseDouble(multString.substring(0, divLoc))
				/ Double.parseDouble(multString.substring(divLoc + 1));
		}

		String moveFormula;
		int checkPenalty;
		if (tokenCount == 4)
		{
			moveFormula = tokens[2];
			try
			{
				checkPenalty = Integer.parseInt(tokens[3]);
			}
			catch (NumberFormatException e)
			{
				return new ParseResult.Fail(
					"Expected " + getTokenName() + " penalty to be an integer, found: " + tokens[3]);
			}
		}
		else
		{
			moveFormula = "";
			checkPenalty = 0;
		}

		info.addLoadMultiplier(tokens[0].toUpperCase(), (float) mult, moveFormula, checkPenalty);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, LoadInfo info)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<LoadInfo> getTokenClass()
	{
		return LoadInfo.class;
	}

	@Override
	public Class<LoadInfo> getDeferredTokenClass()
	{
		return LoadInfo.class;
	}

	@Override
	public boolean process(LoadContext context, LoadInfo info)
	{
		if (info.getLoadMultiplierCount() == 0)
		{
			Logging.errorPrint("Error: load.lst for game mode " + info.getDisplayName()
				+ " does not contain load category definitions. " + "No weight categories will be available. "
				+ "Please refer to the documentation for the Load List file. See: " + info.getSourceURI());
			return false;
		}
		if ((info.getLoadMultiplier("LIGHT") == null) || (info.getLoadMultiplier("MEDIUM") == null)
			|| (info.getLoadMultiplier("HEAVY") == null))
		{
			Logging.errorPrint("Error: load.lst for game mode " + info.getDisplayName()
				+ " does not contain load category definitions " + "for 'Light', 'Medium' and 'Heavy'. "
				+ "Please refer to the documentation for the Load List file. See: " + info.getSourceURI());
			return false;
		}
		return true;
	}
}

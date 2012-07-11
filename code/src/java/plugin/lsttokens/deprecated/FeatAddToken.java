/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.deprecated;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class FeatAddToken implements CDOMSecondaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "FEATADD";
	}

	public String getParentToken()
	{
		return "CHOOSE";
	}

	public ParseResult parseToken(LoadContext context, CDOMObject obj,
		String value)
	{
		if (value == null)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " requires additional arguments", context);
		}
		if (value.indexOf(',') != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments may not contain , : " + value, context);
		}
		if (value.indexOf('[') != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value, context);
		}
		if (value.charAt(0) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments may not start with | : " + value, context);
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments may not end with | : " + value, context);
		}
		if (value.indexOf("||") != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments uses double separator || : " + value, context);
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		while (st.hasMoreTokens())
		{
			String tokString = st.nextToken();
			int equalsLoc = tokString.indexOf("=");
			if (equalsLoc == tokString.length() - 1)
			{
				ComplexParseResult cpr = new ComplexParseResult();
				cpr.addErrorMessage("CHOOSE:" + getTokenName()
					+ " arguments must have value after = : " + tokString);
				cpr.addErrorMessage("  entire token was: " + value);
				return cpr;
			}
		}
		try
		{
			Logging
				.deprecationPrint("CHOOSE:"
					+ getTokenName()
					+ " has been deprecated, "
					+ "please use CHOOSE:FEATSELECTION|x with AUTO:FEAT|%LIST", context);
			boolean proc =
					context.processToken(obj, "CHOOSE", "FEATSELECTION|"
						+ value + "|TITLE=Add a Feat");
			if (!proc)
			{
				return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " encountered an error delegating to "
					+ "CHOOSE:FEATSELECTION|" + value + "|TITLE=Add a Feat", context);
			}
			proc = context.processToken(obj, "AUTO", "FEAT|%LIST");
			if (!proc)
			{
				return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " encountered an error delegating to AUTO:FEAT|%LIST", context);
			}
		}
		catch (PersistenceLayerException e)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " found error : " + e.getMessage(), context);
		}
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		return null;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}

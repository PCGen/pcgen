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
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class ProficiencyToken implements CDOMSecondaryToken<CDOMObject>
{

    @Override
	public String getTokenName()
	{
		return "PROFICIENCY";
	}

    @Override
	public String getParentToken()
	{
		return "CHOOSE";
	}

    @Override
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
		int pipeLoc = value.indexOf("|");
		if (pipeLoc == -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " must have two or more | delimited arguments : " + value, context);
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		if (tok.countTokens() < 3)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " requires at least three arguments: " + value, context);
		}
		String first = tok.nextToken();
		if (!first.equals("ARMOR") && !first.equals("SHIELD")
			&& !first.equals("WEAPON"))
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " first argument was not ARMOR, SHIELD, or WEAPON", context);
		}
		String subtoken = first + "PROFICIENCY";
		String second = tok.nextToken();
		String qualifier;
		if (second.equals("PC"))
		{
			qualifier = "PC";
		}
		else if (second.equals("ALL"))
		{
			qualifier = "ANY";
		}
		else if (second.equals("UNIQUE"))
		{
			qualifier = "!PC";
		}
		else
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " second argument was not PC, ALL, or UNIQUE", context);
		}
		StringBuilder sb = new StringBuilder();
		boolean needPipe = false;
		while (tok.hasMoreTokens())
		{
			if (needPipe == true)
			{
				sb.append('|');
			}
			String tokString = tok.nextToken();
			int equalsLoc = tokString.indexOf("=");
			if (equalsLoc == tokString.length() - 1)
			{
				ComplexParseResult cpr = new ComplexParseResult();
				cpr.addErrorMessage("CHOOSE:" + getTokenName()
					+ " arguments must have value after = : " + tokString);
				cpr.addErrorMessage("  entire token was: " + value);
				return cpr;
			}
			sb.append(tokString);
			needPipe = true;
		}
		Logging.deprecationPrint("CHOOSE:PROFICIENCY|" + first
			+ " has been deprecated, please use CHOOSE:" + subtoken
			+ "|...", context);
		String targetString = sb.toString();
		if (first.equals("WEAPON"))
		{
			targetString = targetString.replaceAll("TYPE\\.", "TYPE=");
		}
		return context.processSubToken(obj, "CHOOSE", subtoken, qualifier + "["
			+ targetString + "]");
	}

    @Override
	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		return null;
	}

    @Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}

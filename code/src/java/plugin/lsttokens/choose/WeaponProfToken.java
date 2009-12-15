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
package plugin.lsttokens.choose;

import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ErrorParsingWrapper;
import pcgen.rules.persistence.token.ParseResult;

public class WeaponProfToken extends ErrorParsingWrapper<CDOMObject> implements CDOMSecondaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "WEAPONPROF";
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
					+ " requires additional arguments");
		}
		if (value.indexOf(',') != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " arguments may not contain , : " + value);
		}
		String suffix = "";
		int bracketLoc;
		while ((bracketLoc = value.lastIndexOf('[')) != -1)
		{
			int closeLoc = value.indexOf("]", bracketLoc);
			if (closeLoc != value.length() - 1)
			{
				return new ParseResult.Fail("CHOOSE:" + getTokenName()
						+ " arguments does not contain matching brackets: "
						+ value);
			}
			String bracketString = value.substring(bracketLoc + 1, closeLoc);
			if ("WEAPONPROF".equals(bracketString))
			{
				// This is okay.
				suffix = "[WEAPONPROF]" + suffix;
			}
			else if (bracketString.startsWith("FEAT="))
			{
				// This is okay.
				suffix = "[" + bracketString + "]" + suffix;
			}
			else
			{
				return new ParseResult.Fail("CHOOSE:" + getTokenName()
						+ " arguments may not contain [" + bracketString
						+ "] : " + value);
			}
			value = value.substring(0, bracketLoc);
		}
		if (value.charAt(0) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " arguments may not start with | : " + value);
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " arguments may not end with | : " + value);
		}
		if (value.indexOf("||") != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " arguments uses double separator || : " + value);
		}
		int pipeLoc = value.indexOf("|");
		if (pipeLoc == -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
							+ " must have two or more | delimited arguments : "
							+ value);
		}
		String start = value.substring(0, pipeLoc);
		int firstarg;
		try
		{
			firstarg = Integer.parseInt(start);
		}
		catch (NumberFormatException nfe)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " first argument must be an Integer : " + value);
		}
		String profs = value.substring(pipeLoc + 1);
		StringTokenizer st = new StringTokenizer(profs, Constants.PIPE);
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
		StringBuilder sb = new StringBuilder();
		sb.append(getTokenName()).append('|').append(firstarg).append('|')
				.append(profs).append(suffix);
		Formula f = FormulaFactory.getFormulaFor(firstarg);
		context.obj.put(obj, FormulaKey.EMBEDDED_NUMCHOICES, f);
		context.obj.put(obj, FormulaKey.EMBEDDED_SELECT, f);
		context.obj.put(obj, StringKey.CHOICE_STRING, sb.toString());
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		String chooseString = context.getObjectContext().getString(cdo,
				StringKey.CHOICE_STRING);
		if (chooseString == null
				|| chooseString.indexOf(getTokenName() + '|') != 0)
		{
			return null;
		}
		return new String[] { chooseString
				.substring(getTokenName().length() + 1) };
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	// TODO Deferred?
	// if (prefix.indexOf("NUMCHOICES=") != -1)
	// {
	// return new ParseResult.Fail("Cannot use NUMCHOICES= with
	// CHOOSE:WEAPONPROF, "
	// + "as it has an integrated choice count");
	// return false;
	// }

}

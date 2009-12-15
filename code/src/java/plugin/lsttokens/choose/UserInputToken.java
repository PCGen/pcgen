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

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ErrorParsingWrapper;
import pcgen.rules.persistence.token.ParseResult;

public class UserInputToken extends ErrorParsingWrapper<CDOMObject> implements CDOMSecondaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "USERINPUT";
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
		if (value.indexOf('[') != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " arguments may not contain [] : " + value);
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
		String title;
		Integer firstarg = null;
		if (pipeLoc == -1)
		{
			title = value;
		}
		else
		{
			String start = value.substring(0, pipeLoc);
			try
			{
				firstarg = Integer.valueOf(start);
			}
			catch (NumberFormatException nfe)
			{
				return new ParseResult.Fail("CHOOSE:" + getTokenName()
						+ " first argument must be an Integer : " + value);
			}
			title = value.substring(pipeLoc + 1);
		}
		if (!title.startsWith("TITLE="))
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " argument must start with TITLE= : " + value);
		}

		ComplexParseResult cpr = new ComplexParseResult();
		if (title.startsWith("TITLE=\""))
		{
			if (!title.endsWith("\""))
			{
				return new ParseResult.Fail("CHOOSE:" + getTokenName()
						+ " argument which starts \" with must end with \" : "
						+ value);
			}
		}
		else
		{
			cpr.addWarningMessage("CHOOSE:" + getTokenName()
					+ " argument TITLE= should use \" around the title : "
					+ value);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(getTokenName()).append('|');
		if (firstarg != null)
		{
			sb.append(firstarg).append('|');
		}
		sb.append(title);
		context.obj.put(obj, StringKey.CHOICE_STRING, sb.toString());
		if (firstarg != null)
		{
			Formula f = FormulaFactory.getFormulaFor(firstarg);
			context.obj.put(obj, FormulaKey.EMBEDDED_SELECT, f);
		}
		return cpr;
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
	// CHOOSE:USERINPUT, "
	// + "as it has an integrated choice count");
	// return false;
	// }
}

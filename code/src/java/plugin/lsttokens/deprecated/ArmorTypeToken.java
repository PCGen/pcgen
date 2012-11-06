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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

public class ArmorTypeToken implements CDOMSecondaryToken<CDOMObject>
{

    @Override
	public String getTokenName()
	{
		return "ARMORTYPE";
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
		ComplexParseResult cpr = new ComplexParseResult();
		cpr.addWarningMessage("CHOOSE:ARMORTYPE has been deprecated.  "
			+ "If you are looking for a replacement function, "
			+ "please contact the PCGen team for support. (Source: " + context.getObjectContext().getSourceURI() + ")");
		if (value == null)
		{
			// No args - legal
			context.obj.put(obj, StringKey.CHOICE_STRING, getTokenName());
			return cpr;
		}
		return new ParseResult.Fail("CHOOSE:" + getTokenName()
			+ " will ignore arguments: " + value, context);
	}

    @Override
	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		String chooseString =
				context.getObjectContext().getString(cdo,
					StringKey.CHOICE_STRING);
		if (chooseString == null)
		{
			return null;
		}
		String returnString;
		if (getTokenName().equals(chooseString))
		{
			returnString = "";
		}
		else
		{
			if (chooseString.indexOf(getTokenName() + '|') == -1)
			{
				return null;
			}
			returnString = chooseString.substring(getTokenName().length() + 1);
		}
		return new String[]{returnString};
	}

    @Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}

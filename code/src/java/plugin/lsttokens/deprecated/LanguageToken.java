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
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class LanguageToken implements CDOMSecondaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "LANGUAGE";
	}

	public String getParentToken()
	{
		return "CHOOSE";
	}

	public ParseResult parseToken(LoadContext context, CDOMObject obj,
		String value)
	{
		Logging.deprecationPrint("CHOOSE:LANGUAGE has been deprecated, "
			+ "please use CHOOSE:LANG.  "
			+ "You may also need AUTO:LANG if used in a skill.", context);

		ParseResult pr =
				context.processSubToken(obj, getParentToken(), "LANG", "TYPE="
					+ value);
		if (!pr.passed())
		{
			return pr;
		}
		try
		{
			if (context.processToken(obj, "AUTO", "LANG|%LIST"))
			{
				return ParseResult.SUCCESS;
			}
			return new ParseResult.Fail("Internal Error: AUTO:LANG failed", context);
		}
		catch (PersistenceLayerException e)
		{
			return new ParseResult.Fail(e.getLocalizedMessage(), context);
		}
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

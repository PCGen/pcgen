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
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class SkillsNamedToken implements CDOMSecondaryToken<CDOMObject>
{

    @Override
	public String getTokenName()
	{
		return "SKILLSNAMED";
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
		Logging.deprecationPrint("CHOOSE:SKILLSNAMED has been deprecated, "
			+ "please use CHOOSE:SKILL|", context);
		String newValue = processSkillMagicalWords(value);
		return context.processSubToken(obj, "CHOOSE", "SKILL", newValue);
	}

	private String processSkillMagicalWords(String value)
	{
		StringTokenizer st = new StringTokenizer(value, "|", true);
		StringBuilder sb = new StringBuilder();
		while (st.hasMoreTokens())
		{
			String tok = st.nextToken();
			if ("CROSSCLASS".equalsIgnoreCase(tok))
			{
				tok = "CROSSCLASS";
			}
			if ("CLASS".equalsIgnoreCase(tok))
			{
				tok = "CLASS";
			}
			if ("EXCLUSIVE".equalsIgnoreCase(tok))
			{
				tok = "EXCLUSIVE";
			}
			if ("NORANK".equalsIgnoreCase(tok))
			{
				tok = "NORANK";
			}
			if (tok.regionMatches(true, 0, "RANKS=", 0, 6))
			{
				tok = "RANKS=" + tok.substring(6);
			}
			sb.append(tok);
		}
		return sb.toString();
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

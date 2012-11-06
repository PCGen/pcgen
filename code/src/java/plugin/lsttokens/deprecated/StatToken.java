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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class StatToken implements CDOMSecondaryToken<CDOMObject>
{

    @Override
	public String getTokenName()
	{
		return "STAT";
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
		Logging.deprecationPrint("CHOOSE:STAT has been deprecated, "
			+ "please use CHOOSE:PCSTAT", context);
		if (value == null)
		{
			// No args - use all stats - legal
			return context.processSubToken(obj, "CHOOSE", "PCSTAT", "ALL");
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
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		Collection<PCStat> list =
				context.ref.getConstructedCDOMObjects(PCStat.class);
		List<PCStat> subList = new ArrayList<PCStat>(list);
		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			PCStat stat =
					context.ref.getAbbreviatedObject(PCStat.class, tokText);
			if (stat == null)
			{
				Logging.deprecationPrint("Did not find STAT: " + tokText
					+ " used in CHOOSE: " + value, context);
				continue;
			}
			subList.remove(stat);
		}
		StringBuilder all = new StringBuilder();
		boolean needPipe = false;
		for (PCStat pcs : subList)
		{
			if (needPipe)
			{
				all.append('|');
			}
			all.append(pcs.getAbb());
			needPipe = true;
		}
		return context.processSubToken(obj, "CHOOSE", "PCSTAT", all.toString());
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

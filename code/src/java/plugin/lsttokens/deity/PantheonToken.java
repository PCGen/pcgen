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
package plugin.lsttokens.deity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Pantheon;
import pcgen.core.Deity;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with PANTHEON Token
 */
public class PantheonToken extends AbstractTokenWithSeparator<Deity> implements
		CDOMPrimaryToken<Deity>
{

	@Override
	public String getTokenName()
	{
		return "PANTHEON";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context, Deity deity, String value)
	{
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		boolean first = true;
		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tokText))
			{
				if (!first)
				{
					return new ParseResult.Fail("  Non-sensical "
							+ getTokenName()
							+ ": .CLEAR was not the first list item", context);
				}
				context.getObjectContext().removeList(deity, ListKey.PANTHEON);
			}
			else
			{
				context.getObjectContext().addToList(deity, ListKey.PANTHEON,
					Pantheon.getConstant(tokText));
			}
			first = false;
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Deity deity)
	{
		Changes<Pantheon> changes =
				context.getObjectContext().getListChanges(deity,
					ListKey.PANTHEON);
		Collection<Pantheon> removedItems = changes.getRemoved();
		List<String> list = new ArrayList<String>();
		if (removedItems != null && !removedItems.isEmpty())
		{
			context.addWriteMessage(getTokenName()
				+ " does not support .CLEAR.");
			return null;
		}
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR);
		}
		Collection<Pantheon> added = changes.getAdded();
		if (added != null && !added.isEmpty())
		{
			list.add(StringUtil.join(changes.getAdded(), Constants.PIPE));
		}
		if (list.isEmpty())
		{
			return null;
		}
		return list.toArray(new String[list.size()]);
	}

	@Override
	public Class<Deity> getTokenClass()
	{
		return Deity.class;
	}
}

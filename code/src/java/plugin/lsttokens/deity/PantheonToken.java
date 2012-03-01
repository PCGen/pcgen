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
		while (tok.hasMoreTokens())
		{
			context.getObjectContext().addToList(deity, ListKey.PANTHEON,
					Pantheon.getConstant(tok.nextToken()));
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Deity deity)
	{
		Changes<Pantheon> changes =
				context.getObjectContext().getListChanges(deity,
					ListKey.PANTHEON);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		return new String[]{StringUtil.join(changes.getAdded(), Constants.PIPE)};
	}

	@Override
	public Class<Deity> getTokenClass()
	{
		return Deity.class;
	}
}

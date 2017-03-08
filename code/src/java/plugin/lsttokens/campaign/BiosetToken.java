/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.campaign;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with BIOSET Token
 */
public class BiosetToken extends AbstractTokenWithSeparator<Campaign> implements
		CDOMPrimaryToken<Campaign>
{

	@Override
	public String getTokenName()
	{
		return "BIOSET";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context,
	                                              Campaign obj, String value)
	{
		CampaignSourceEntry cse = context.getCampaignSourceEntry(obj, value);
		if (cse == null)
		{
			//Error
			return ParseResult.INTERNAL_ERROR;
		}
		if (!cse.getIncludeItems().isEmpty())
		{
			return new ParseResult.Fail(getTokenName() + " does not allow INCLUDE: "
				+ value, context);
		}
		if (!cse.getExcludeItems().isEmpty())
		{
			return new ParseResult.Fail(getTokenName() + " does not allow EXCLUDE: "
				+ value, context);
		}
		context.getObjectContext().addToList(obj, ListKey.FILE_BIO_SET, cse);
		return ParseResult.SUCCESS;
	}

    @Override
	public String[] unparse(LoadContext context, Campaign obj)
	{
		Changes<CampaignSourceEntry> cseChanges =
				context.getObjectContext().getListChanges(obj, ListKey.FILE_BIO_SET);
		Collection<CampaignSourceEntry> added = cseChanges.getAdded();
		if (added == null)
		{
			//empty indicates no token
			return null;
		}
		Set<String> set = new TreeSet<>();
		for (CampaignSourceEntry cse : added)
		{
			set.add(cse.getLSTformat());
		}
		return set.toArray(new String[set.size()]);
	}

    @Override
	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}
}

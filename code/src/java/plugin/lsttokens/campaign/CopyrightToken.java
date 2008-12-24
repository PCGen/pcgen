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
package plugin.lsttokens.campaign;

import java.net.URI;
import java.util.Collection;
import java.util.TreeSet;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.InstallLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with COPYRIGHT Token
 */
public class CopyrightToken extends AbstractToken implements
		CDOMPrimaryToken<Campaign>, InstallLstToken
{

	@Override
	public String getTokenName()
	{
		return "COPYRIGHT";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addToListFor(ListKey.SECTION_15, value);
		return true;
	}

	public boolean parse(LoadContext context, Campaign campaign, String value)
		throws PersistenceLayerException
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.obj.addToList(campaign, ListKey.SECTION_15, value);
		return true;
	}

	public String[] unparse(LoadContext context, Campaign campaign)
	{
		Changes<String> changes =
				context.getObjectContext().getListChanges(campaign,
					ListKey.SECTION_15);
		TreeSet<String> set = new TreeSet<String>();
		Collection<String> added = changes.getAdded();
		if (added != null && !added.isEmpty())
		{
			set.addAll(added);
		}
		if (set.isEmpty())
		{
			context.addWriteMessage(getTokenName()
				+ " was expecting non-empty changes to include "
				+ "added items or global clear");
			return null;
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}

}

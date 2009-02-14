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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with LICENSE Token
 */
public class LicenseToken extends AbstractToken implements
		CDOMPrimaryToken<Campaign>
{

	@Override
	public String getTokenName()
	{
		return "LICENSE";
	}

	public boolean parse(LoadContext context, Campaign campaign, String value)
		throws PersistenceLayerException
	{
		if (isEmpty(value))
		{
			return false;
		}
		if (value.startsWith("FILE="))
		{
			String fileURI = value.substring(5);
			if (fileURI.length() == 0)
			{
				Logging.errorPrint("Cannot have empty FILE in "
						+ getTokenName());
				return false;
			}
			CampaignSourceEntry cse = context.getCampaignSourceEntry(campaign, fileURI);
			if (cse == null)
			{
				//Error
				return false;
			}
			context.obj.addToList(campaign, ListKey.LICENSE_FILE, cse);
		}
		else
		{
			context.obj.addToList(campaign, ListKey.LICENSE, value);
		}
		return true;
	}

	public String[] unparse(LoadContext context, Campaign campaign)
	{
		Changes<String> changes =
				context.getObjectContext().getListChanges(campaign, ListKey.LICENSE);
		Changes<CampaignSourceEntry> filechanges =
				context.getObjectContext().getListChanges(campaign,
					ListKey.LICENSE_FILE);
		List<String> set = new ArrayList<String>();
		Collection<String> added = changes.getAdded();
		if (added != null && !added.isEmpty())
		{
			set.addAll(added);
		}
		Collection<CampaignSourceEntry> addeduri = filechanges.getAdded();
		if (addeduri != null && !addeduri.isEmpty())
		{
			for (CampaignSourceEntry cse : addeduri)
			{
				set.add("FILE=" + cse.getLSTformat());
			}
		}
		if (set.isEmpty())
		{
			//Okay, no license info
			return null;
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}

}

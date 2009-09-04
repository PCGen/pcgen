/*
 * Copyright (c) Thomas Parker, 2009.
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
package pcgen.cdom.facet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.enumeration.CharID;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceManager;

public class CampaignFacet
{

	public Collection<Campaign> getRootCampaigns(CharID id)
	{
		return PersistenceManager.getInstance().getLoadedCampaigns();
	}

	public Collection<Campaign> getSet(CharID id)
	{
		Set<Campaign> results = new HashSet<Campaign>();
		for (Campaign c : getRootCampaigns(id))
		{
			results.add(c);
			results.addAll(c.getSubCampaigns());
		}
		return results;
	}

}

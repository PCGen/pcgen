/*
 * DestToken.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package plugin.lsttokens.campaign.installable;

import java.net.URI;

import pcgen.cdom.enumeration.Destination;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Campaign;
import pcgen.core.InstallableCampaign;
import pcgen.persistence.lst.InstallLstToken;
import pcgen.util.Logging;

/**
 * {@code DestToken} parses DEST tokens in installable campaigns.
 */
public class DestToken implements InstallLstToken
{

	@Override
	public String getTokenName()
	{
		return "DEST";
	}

	@Override
	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		if (!(campaign instanceof InstallableCampaign ic))
		{
			Logging.log(Logging.ERROR, "Campaign " + campaign.getDisplayName() + " is not an installable campaign.");
			return false;
		}

		if (value.equals("DATA"))
		{
			ic.put(ObjectKey.DESTINATION, Destination.DATA);
		}
		else if (value.equals("VENDORDATA"))
		{
			ic.put(ObjectKey.DESTINATION, Destination.VENDORDATA);
		}
		else
		{
			Logging.log(Logging.LST_ERROR,
				"DEST value '" + value + "' not valid for campaign " + campaign.getDisplayName());
			return false;
		}

		return true;
	}
}

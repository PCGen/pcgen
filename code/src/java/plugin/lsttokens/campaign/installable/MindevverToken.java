/*
 * MindevverToken.java
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

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.core.InstallableCampaign;
import pcgen.persistence.lst.InstallLstToken;
import pcgen.util.Logging;

/**
 * {@code MinverToken} parses MINDEVVER tokens in installable campaigns.
 */
public class MindevverToken implements InstallLstToken
{

	@Override
	public String getTokenName()
	{
		return "MINDEVVER";
	}

	@Override
	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		if (!(campaign instanceof InstallableCampaign ic))
		{
			Logging.log(Logging.ERROR, "Campaign " + campaign.getDisplayName() + " is not an installable campaign.");
			return false;
		}
		ic.put(StringKey.MINDEVVER, value != null ? value.trim() : "");
		return true;
	}
}

/*
 * AbilityCategoryToken.java
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
 *
 * Created on November 20, 2007
 */
package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

/**
 * <code>AbilityCategoryToken</code> parses ABILITYCATEGORY in campaign (pcc)
 * files.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class AbilityCategoryToken implements CampaignLstToken
{

	/**
	 * Get the token name
	 * @return token name
	 */
	public String getTokenName()
	{
		return "ABILITYCATEGORY";
	}

	/**
	 * Parse the ability category token
	 * 
	 * @param campaign The campaign being processed.
	 * @param value The value of the AbilityCategory token.
	 * @param sourceUrl The URI to the pcc file
	 * @return true
	 */
	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addLine("ABILITYCATEGORY:" + value);
		campaign.addAbilityCategoryFile(CampaignSourceEntry.getNewCSE(campaign,
				sourceUri, value));
		return true;
	}
}

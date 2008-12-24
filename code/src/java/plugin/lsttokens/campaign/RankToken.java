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

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with RANK Token
 */
public class RankToken implements CDOMPrimaryToken<Campaign>
{

	public String getTokenName()
	{
		return "RANK";
	}

	public boolean parse(LoadContext context, Campaign campaign, String value)
		throws PersistenceLayerException
	{
		try
		{
			context.obj.put(campaign, IntegerKey.CAMPAIGN_RANK, Integer
				.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.log(Logging.LST_ERROR, "Bad " + getTokenName()
				+ " value, expected an integer: " + value);
		}
		return false;
	}

	public String[] unparse(LoadContext context, Campaign campaign)
	{
		Integer rank =
				context.getObjectContext().getInteger(campaign,
					IntegerKey.CAMPAIGN_RANK);
		if (rank == null)
		{
			return null;
		}
		return new String[]{rank.toString()};
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}

}

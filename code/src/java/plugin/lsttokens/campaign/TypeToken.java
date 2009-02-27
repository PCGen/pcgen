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

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 *
 */
public class TypeToken extends AbstractToken implements
		CDOMPrimaryToken<Campaign>
{

	@Override
	public String getTokenName()
	{
		return "TYPE";
	}

	public boolean parse(LoadContext context, Campaign campaign, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('.', value))
		{
			return false;
		}

		StringTokenizer aTok = new StringTokenizer(value, Constants.DOT);
		String dataProducer = aTok.nextToken();
		context.getObjectContext().put(campaign, StringKey.DATA_PRODUCER,
			dataProducer);
		if (aTok.hasMoreTokens())
		{
			String dataFormat = aTok.nextToken();
			context.getObjectContext().put(campaign, StringKey.DATA_FORMAT,
				dataFormat);
		}
		else
		{
			context.getObjectContext().put(campaign, StringKey.DATA_FORMAT,
					null);
		}
		if (aTok.hasMoreTokens())
		{
			String campaignSetting = aTok.nextToken();
			context.getObjectContext().put(campaign,
				StringKey.CAMPAIGN_SETTING, campaignSetting);
		}
		else
		{
			context.getObjectContext().put(campaign,
					StringKey.CAMPAIGN_SETTING, null);
		}
		if (aTok.hasMoreTokens())
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
				+ " in Campaign may have a"
				+ " maximum of 3 items, value is invalid: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, Campaign campaign)
	{
		String producer =
				context.getObjectContext().getString(campaign,
					StringKey.DATA_PRODUCER);
		if (producer == null)
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(producer);
		String format =
				context.getObjectContext().getString(campaign,
					StringKey.DATA_FORMAT);
		if (format != null)
		{
			sb.append(Constants.DOT);
			sb.append(format);
		}
		String setting =
				context.getObjectContext().getString(campaign,
					StringKey.CAMPAIGN_SETTING);
		if (setting != null)
		{
			sb.append(Constants.DOT);
			sb.append(setting);
		}
		return new String[]{sb.toString()};
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}
}

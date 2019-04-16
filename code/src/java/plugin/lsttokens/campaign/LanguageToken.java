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

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.persistence.token.AbstractBasicCampaignToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with LANGUAGE Token
 */
public class LanguageToken extends AbstractBasicCampaignToken implements CDOMPrimaryToken<Campaign>
{

	@Override
	public String getTokenName()
	{
		return "LANGUAGE";
	}

	@Override
	protected ListKey<CampaignSourceEntry> getListKey()
	{
		return ListKey.FILE_LANGUAGE;
	}
}

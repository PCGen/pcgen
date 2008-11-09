package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

/**
 * Class deals with SHOWINMENU Token
 */
public class ShowinmenuToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "SHOWINMENU";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		boolean show =
				value.startsWith("Y") || Boolean.valueOf(value).booleanValue();
		campaign.setShowInMenu(show);
		return true;
	}
}

package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with SHOWINMENU Token
 */
public class ShowinmenuToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "SHOWINMENU";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUl)
	{
		campaign.setShowInMenu(Boolean.valueOf(value).booleanValue());
		return true;
	}
}

package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with HELP Token
 */
public class HelpToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "HELP";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl)
	{
		campaign.setHelp(value);
		campaign.addLine("HELP:" + value);
		return true;
	}
}

package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

/**
 * Class deals with HELP Token
 */
public class HelpToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "HELP";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.setHelp(value);
		campaign.addLine("HELP:" + value);
		return true;
	}
}

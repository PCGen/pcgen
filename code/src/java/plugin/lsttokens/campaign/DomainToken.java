package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with DOMAIN Token
 */
public class DomainToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "DOMAIN";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl)
	{
		campaign.addLine("DOMAIN:" + value);
		campaign.addDomainFile(new CampaignSourceEntry(campaign, CampaignLoader
			.convertFilePath(sourceUrl, value)));
		return true;
	}
}

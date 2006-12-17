package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with COVER Token
 */
public class CoverToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "COVER";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl)
	{
		campaign.addLine("COVER:" + value);
		campaign.addCoverFile(new CampaignSourceEntry(campaign, CampaignLoader
			.convertFilePath(sourceUrl, value)));
		return true;
	}
}

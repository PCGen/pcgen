package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with COMPANIONMOD Token
 */
public class CompanionmodToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "COMPANIONMOD";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl)
	{
		campaign.addLine("COMPANIONMOD:" + value);
		campaign.addCompanionModFile(new CampaignSourceEntry(campaign,
			CampaignLoader.convertFilePath(sourceUrl, value)));
		return true;
	}
}

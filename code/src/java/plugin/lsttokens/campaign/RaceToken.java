package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with RACE Token
 */
public class RaceToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "RACE";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl)
	{
		campaign.addLine("RACE:" + value);
		campaign.addRaceFile(new CampaignSourceEntry(campaign, CampaignLoader
			.convertFilePath(sourceUrl, value)));
		return true;
	}
}

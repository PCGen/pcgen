package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with KIT Token
 */
public class KitToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "KIT";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl)
	{
		campaign.addLine("KIT:" + value);
		campaign.addKitFile(new CampaignSourceEntry(campaign, CampaignLoader
			.convertFilePath(sourceUrl, value)));
		return true;
	}
}

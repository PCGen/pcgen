package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with TEMPLATE Token
 */
public class TemplateToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "TEMPLATE";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl)
	{
		campaign.addLine("TEMPLATE:" + value);
		campaign.addTemplateFile(new CampaignSourceEntry(campaign,
			CampaignLoader.convertFilePath(sourceUrl, value)));
		return true;
	}
}

package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with EQUIPMENT Token
 */
public class EquipmentToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "EQUIPMENT";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl)
	{
		campaign.addLine("EQUIPMENT:" + value);
		campaign.addEquipFile(new CampaignSourceEntry(campaign, CampaignLoader
			.convertFilePath(sourceUrl, value)));
		return true;
	}
}

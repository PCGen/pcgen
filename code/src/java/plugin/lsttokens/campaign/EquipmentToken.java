package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

/**
 * Class deals with EQUIPMENT Token
 */
public class EquipmentToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "EQUIPMENT";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addLine("EQUIPMENT:" + value);
		campaign.addEquipFile(CampaignSourceEntry.getNewCSE(campaign,
				sourceUri, value));
		return true;
	}
}

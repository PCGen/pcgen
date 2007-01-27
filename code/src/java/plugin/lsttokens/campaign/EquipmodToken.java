package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

/**
 * Class deals with EQUIPMOD Token
 */
public class EquipmodToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "EQUIPMOD";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addLine("EQUIPMOD:" + value);
		campaign.addEquipModFile(CampaignSourceEntry.getNewCSE(campaign,
				sourceUri, value));
		return true;
	}
}

package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

/**
 * Class deals with COMPANIONMOD Token
 */
public class CompanionmodToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "COMPANIONMOD";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addLine("COMPANIONMOD:" + value);
		campaign.addCompanionModFile(CampaignSourceEntry.getNewCSE(campaign,
				sourceUri, value));
		return true;
	}
}

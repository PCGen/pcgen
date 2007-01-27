package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

/**
 * Class deals with ABILITY Token for campaign
 */
public class AbilityToken implements CampaignLstToken
{

	/**
	 * Get the token name
	 * @return token name
	 */
	public String getTokenName()
	{
		return "ABILITY";
	}

	/**
	 * Parse the ability token
	 * 
	 * @param campaign 
	 * @param value 
	 * @param sourceUrl 
	 * @return true
	 */
	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addLine("ABILITY:" + value);
		campaign.addAbilityFile(CampaignSourceEntry.getNewCSE(campaign,
				sourceUri, value));
		return true;
	}
}

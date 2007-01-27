package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

/**
 * Class deals with LANGUAGE Token
 */
public class LanguageToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "LANGUAGE";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addLine("LANGUAGE:" + value);
		campaign.addLanguageFile(CampaignSourceEntry.getNewCSE(campaign,
				sourceUri, value));
		return true;
	}
}

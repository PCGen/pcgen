package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

/**
 * Class deals with CAMPAIGN Token
 */
public class CampaignToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "CAMPAIGN";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.setName(value);
		campaign.setSourceURI(sourceUri);
		return true;
	}
}

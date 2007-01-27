package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

/**
 * Class deals with PUBNAMESHORT Token
 */
public class PubnameshortToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "PUBNAMESHORT";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.setPubNameShort(value);
		return true;
	}
}

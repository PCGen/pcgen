package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

/**
 * Class deals with PUBNAMELONG Token
 */
public class PubnamelongToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "PUBNAMELONG";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.setPubNameLong(value);
		return true;
	}
}

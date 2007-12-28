package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.InstallLstToken;

/**
 * Class deals with PUBNAMELONG Token
 */
public class PubnamelongToken implements CampaignLstToken, InstallLstToken
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

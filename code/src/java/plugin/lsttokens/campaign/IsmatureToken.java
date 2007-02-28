package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

/**
 * Class deals with ISOGL Token
 */
public class IsmatureToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "ISMATURE";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.setIsMature(value.startsWith("Y"));
		return true;
	}
}

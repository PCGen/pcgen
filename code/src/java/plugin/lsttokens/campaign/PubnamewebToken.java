package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

/**
 * Class deals with PUBNAMEWEB Token
 */
public class PubnamewebToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "PUBNAMEWEB";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.setPubNameWeb(value);
		return true;
	}
}

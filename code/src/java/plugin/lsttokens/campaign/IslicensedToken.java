package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

/**
 * Class deals with ISLICENSED Token
 */
public class IslicensedToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "ISLICENSED";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.setIsLicensed(value.startsWith("Y"));
		return true;
	}
}

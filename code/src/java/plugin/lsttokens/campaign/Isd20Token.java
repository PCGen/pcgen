package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

/**
 * Class deals with ISD20 Token
 */
public class Isd20Token implements CampaignLstToken
{

	public String getTokenName()
	{
		return "ISD20";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.setIsD20(value.startsWith("Y"));
		return true;
	}
}

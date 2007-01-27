package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

/**
 * Class deals with COPYRIGHT Token
 */
public class CopyrightToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "COPYRIGHT";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addSection15(value);
		return true;
	}
}

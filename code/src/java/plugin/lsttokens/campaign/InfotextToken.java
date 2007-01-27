package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

/**
 * Class deals with INFOTEXT Token
 */
public class InfotextToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "INFOTEXT";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.setInfoText(value);
		return true;
	}
}

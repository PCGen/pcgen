package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

/**
 * Class deals with ISOGL Token
 */
public class IsoglToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "ISOGL";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.setIsOGL(value.startsWith("Y"));
		return true;
	}
}

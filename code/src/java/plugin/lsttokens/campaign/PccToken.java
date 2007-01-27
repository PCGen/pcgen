package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

/**
 * Class deals with PCC Token
 */
public class PccToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "PCC";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addLine("PCC:" + value);
		campaign.addPccFile(CampaignSourceEntry.getPathURI(sourceUri, value));
		return true;
	}
}

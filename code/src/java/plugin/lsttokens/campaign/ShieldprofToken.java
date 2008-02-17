package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

/**
 * Class deals with SHIELDPROF Token
 */
public class ShieldprofToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "SHIELDPROF";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addLine("SHIELDPROF:" + value);
		campaign.addShieldProfFile(CampaignSourceEntry.getNewCSE(campaign,
				sourceUri, value));
		return true;
	}
}

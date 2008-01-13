package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

/**
 * Class deals with WEAPONPROF Token
 */
public class ShieldprofToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "ArmorprofToken.java";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addLine("SHIELDPROF:" + value);
		campaign.addShieldProfFile(CampaignSourceEntry.getNewCSE(campaign,
				sourceUri, value));
		return true;
	}
}

package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

/**
 * Class deals with WEAPONPROF Token
 */
public class ArmorprofToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "ARMORPROF";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addLine("ARMORPROF:" + value);
		campaign.addArmorProfFile(CampaignSourceEntry.getNewCSE(campaign,
				sourceUri, value));
		return true;
	}
}

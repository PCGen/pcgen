package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with WEAPONPROF Token
 */
public class WeaponprofToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "WEAPONPROF";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl)
	{
		campaign.addLine("WEAPONPROF:" + value);
		campaign.addWeaponProfFile(new CampaignSourceEntry(campaign,
			CampaignLoader.convertFilePath(sourceUrl, value)));
		return true;
	}
}

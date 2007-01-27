package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

/**
 * Class deals with SETTING Token
 */
public class SettingToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "SETTING";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.setSetting(value);
		return true;
	}
}

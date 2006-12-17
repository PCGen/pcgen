package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with SETTING Token
 */
public class SettingToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "SETTING";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUl)
	{
		campaign.setSetting(value);
		return true;
	}
}

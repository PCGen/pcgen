package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with GAMEMODE Token
 */
public class GamemodeToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "GAMEMODE";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl)
	{
		campaign.setGameMode(value);
		return true;
	}
}

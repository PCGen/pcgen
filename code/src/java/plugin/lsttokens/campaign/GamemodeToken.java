package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

/**
 * Class deals with GAMEMODE Token
 */
public class GamemodeToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "GAMEMODE";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.setGameMode(value);
		return true;
	}
}

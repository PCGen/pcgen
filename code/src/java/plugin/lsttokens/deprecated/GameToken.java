package plugin.lsttokens.deprecated;

import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.DeprecatedToken;

import java.net.URI;

/**
 * Class deals with GAME Token
 */
public class GameToken implements CampaignLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "GAME";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.setGameMode(value);
		return true;
	}

	public String getMessage(PObject obj, String value)
	{
		return "Use GAMEMODE: instead.";
	}
}

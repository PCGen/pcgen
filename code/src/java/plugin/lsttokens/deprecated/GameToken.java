package plugin.lsttokens.deprecated;

import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.Deprecated;

import java.net.URL;

/**
 * Class deals with GAME Token
 */
public class GameToken implements CampaignLstToken, Deprecated {

	public String getTokenName() {
		return "GAME";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.setGameMode(value);
		return true;
	}

	public String getMessage(PObject obj, String value) {
		return "Use GAMEMODE: instead.";
	}
}

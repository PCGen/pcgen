package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with BABATTCYC Token
 */
public class BabattcycToken implements GameModeLstToken {

	public String getTokenName() {
		return "BABATTCYC";
	}

	public boolean parse(GameMode gameMode, String value) {
		try {
			gameMode.setBabAttCyc(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

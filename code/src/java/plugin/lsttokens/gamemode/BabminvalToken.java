package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with BABMINVAL Token
 */
public class BabminvalToken implements GameModeLstToken {

	public String getTokenName() {
		return "BABMINVAL";
	}

	public boolean parse(GameMode gameMode, String value) {
		try {
			gameMode.setBabMinVal(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

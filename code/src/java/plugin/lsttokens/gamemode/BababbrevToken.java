package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with BABABBREV Token
 */
public class BababbrevToken implements GameModeLstToken {

	public String getTokenName() {
		return "BABABBREV";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.setBabAbbrev(value);
		return true;
	}
}

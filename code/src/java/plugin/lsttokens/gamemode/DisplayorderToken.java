package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with DEITY Token
 */
public class DisplayorderToken implements GameModeLstToken {

	public String getTokenName() {
		return "DISPLAYORDER";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.setDisplayOrder(value);
		return true;
	}
}

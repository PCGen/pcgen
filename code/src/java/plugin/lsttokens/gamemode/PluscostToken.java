package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with PLUSCOST Token
 */
public class PluscostToken implements GameModeLstToken {

	public String getTokenName() {
		return "PLUSCOST";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.addPlusCalculation(value);
		return true;
	}
}

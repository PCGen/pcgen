package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with BONUSSTATLEVELSTARTINTERVAL Token
 */
public class BonusstatlevelstartintervalToken implements GameModeLstToken {

	public String getTokenName() {
		return "BONUSSTATLEVELSTARTINTERVAL";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.setBonusStatLevels(value);
		return true;
	}
}

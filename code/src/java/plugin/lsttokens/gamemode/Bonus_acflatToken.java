package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with BONUS_ACFLAT Token
 */
public class Bonus_acflatToken implements GameModeLstToken {

	public String getTokenName() {
		return "BONUS_ACFLAT";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.setAcFlatBonus(value);
		return true;
	}
}

package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with HPFORMULA Token
 */
public class HpformulaToken implements GameModeLstToken {

	public String getTokenName() {
		return "HPFORMULA";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.setHPFormula(value);
		return true;
	}
}

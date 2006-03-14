package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with CURRENCYUNITABBREV Token
 */
public class CurrencyunitabbrevToken implements GameModeLstToken {

	public String getTokenName() {
		return "CURRENCYUNITABBREV";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.setCurrencyUnitAbbrev(value);
		return true;
	}
}

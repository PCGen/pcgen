package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with HPNAME Token
 */
public class HpnameToken implements GameModeLstToken {

	public String getTokenName() {
		return "HPNAME";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.setHPText(value);
		return true;
	}
}

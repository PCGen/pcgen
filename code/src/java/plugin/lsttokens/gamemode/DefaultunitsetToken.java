package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with DEFAULTUNITSET Token
 */
public class DefaultunitsetToken implements GameModeLstToken {

	public String getTokenName() {
		return "DEFAULTUNITSET";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.setDefaultUnitSet(value);
		return true;
	}
}

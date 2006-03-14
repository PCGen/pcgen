package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with DISPLAYVARIABLE1NAME Token
 */
public class Displayvariable1nameToken implements GameModeLstToken {

	public String getTokenName() {
		return "DISPLAYVARIABLE1NAME";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.setVariableDisplayName(value);
		return true;
	}
}

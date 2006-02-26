package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with DISPLAYVARIABLE3NAME Token
 */
public class Displayvariable3nameToken implements GameModeLstToken {

	public String getTokenName() {
		return "DISPLAYVARIABLE3NAME";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.setVariableDisplay3Name(value);
		return true;
	}
}

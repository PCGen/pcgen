package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with DISPLAYVARIABLE3TEXT Token
 */
public class Displayvariable3textToken implements GameModeLstToken {

	public String getTokenName() {
		return "DISPLAYVARIABLE3TEXT";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.setVariableDisplay3Text(value);
		return true;
	}
}

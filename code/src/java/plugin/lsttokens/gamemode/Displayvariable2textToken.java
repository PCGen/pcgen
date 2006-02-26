package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with DISPLAYVARIABLE2TEXT Token
 */
public class Displayvariable2textToken implements GameModeLstToken {

	public String getTokenName() {
		return "DISPLAYVARIABLE2TEXT";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.setVariableDisplay2Text(value);
		return true;
	}
}

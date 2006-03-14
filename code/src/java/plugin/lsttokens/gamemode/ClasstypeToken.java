package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with CLASSTYPE Token
 */
public class ClasstypeToken implements GameModeLstToken {

	public String getTokenName() {
		return "CLASSTYPE";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.addClassType(value);
		return true;
	}
}

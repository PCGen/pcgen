package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with MENUENTRY Token
 */
public class MenuentryToken implements GameModeLstToken {

	public String getTokenName() {
		return "MENUENTRY";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.setModeName(value.replace('|', '\n'));
		return true;
	}
}

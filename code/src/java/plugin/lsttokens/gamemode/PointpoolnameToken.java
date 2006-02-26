package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with POINTPOOLNAME Token
 */
public class PointpoolnameToken implements GameModeLstToken {

	public String getTokenName() {
		return "POINTPOOLNAME";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.setPointPoolName(value.replace('|', '\n'));
		return true;
	}
}

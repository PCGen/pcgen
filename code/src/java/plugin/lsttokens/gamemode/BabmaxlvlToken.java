package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with BABMAXLVL Token
 */
public class BabmaxlvlToken implements GameModeLstToken {

	public String getTokenName() {
		return "BABMAXLVL";
	}

	public boolean parse(GameMode gameMode, String value) {
		try {
			gameMode.setBabMaxLvl(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

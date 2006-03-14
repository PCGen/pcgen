package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.persistence.lst.UnitSetLoader;

/**
 * Class deals with UNITSET Token
 */
public class UnitsetToken implements GameModeLstToken {

	public String getTokenName() {
		return "UNITSET";
	}

	public boolean parse(GameMode gameMode, String value) {
		try {
			UnitSetLoader unitSetLoader = new UnitSetLoader();
			unitSetLoader.parseLine(gameMode, "UNITSET:" + value);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
}

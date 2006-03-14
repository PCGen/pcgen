package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SKILLCOST_CLASS Token
 */
public class SkillmultiplierToken implements GameModeLstToken {

	public String getTokenName() {
		return "SKILLMULTIPLIER";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.setSkillMultiplierLevels(value);
		return true;
	}
}

package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SKILLCOST_EXCLUSIVE Token
 */
public class Skillcost_exclusiveToken implements GameModeLstToken {

	public String getTokenName() {
		return "SKILLCOST_EXCLUSIVE";
	}

	public boolean parse(GameMode gameMode, String value) {
		try {
			gameMode.setSkillCost_Exclusive(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

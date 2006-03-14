package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with RANKMODFORMULA Token
 */
public class RankmodformulaToken implements GameModeLstToken {

	public String getTokenName() {
		return "RANKMODFORMULA";
	}

	public boolean parse(GameMode gameMode, String value) {
		gameMode.setRankModFormula(value);
		return true;
	}
}

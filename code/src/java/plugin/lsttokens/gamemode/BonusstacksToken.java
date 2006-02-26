package plugin.lsttokens.gamemode;

import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;


/**
 * Class deals with BONUSSTACKS Token
 */
public class BonusstacksToken implements GameModeLstToken {

	public String getTokenName() {
		return "BONUSSTACKS";
	}

	public boolean parse(GameMode gameMode, String value) {
		StringTokenizer tok = new StringTokenizer(value, ".");
		while(tok.hasMoreTokens()) {
			final String type = tok.nextToken();
			if ("CLEAR".equals(type)) {
				gameMode.clearBonusStacksList();
			}
			else {
				gameMode.addToBonusStackList(type.toUpperCase());
			}
		}
		return true;
	}
}

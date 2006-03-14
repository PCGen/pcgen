package plugin.lsttokens.gamemode;

import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SHOWTAB Token
 */
public class ShowtabToken implements GameModeLstToken {

	public String getTokenName() {
		return "SHOWTAB";
	}

	public boolean parse(GameMode gameMode, String value) {
		final StringTokenizer tok = new StringTokenizer(value, "|");
		final String tabName = tok.nextToken();
		final String visibility = tok.nextToken();
		final int tab = GameMode.getTabNumber(tabName);

		if (tab == Constants.TAB_INVALID) {
			return false;
		}

		gameMode.setTabVisible(tab, visibility.toUpperCase().startsWith("Y"));
		return true;
	}
}

package plugin.lsttokens.gamemode.tab;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.persistence.lst.TabLoader;
import pcgen.persistence.lst.TabLstToken;
import pcgen.util.enumeration.Tab;

/**
 * Class deals with STATTABLEHIDDENCOLUMNS Token
 */
public class StattablehiddencolumnsToken implements TabLstToken {

	public String getTokenName() {
		return "STATTABLEHIDDENCOLUMNS";
	}

	public boolean parse(GameMode gameMode, Map<String, String> tab, String value) {
		final Tab aTab = GameMode.getTab(tab.get(TabLoader.TAB));
		if (aTab != Tab.SUMMARY) {
			return false;
		}

		for(int i = 0; i < 7; ++i) {
			gameMode.setSummaryTabStatColumnVisible(i, true);
		}
		final StringTokenizer commaTok = new StringTokenizer(value, ",");
		while (commaTok.hasMoreTokens()) {
			String commaToken = commaTok.nextToken();
			try {
				gameMode.setSummaryTabStatColumnVisible(Integer.parseInt(commaToken), false);
			}
			catch (NumberFormatException nfe) {
				return false;
			}
		}
		return true;
	}
}

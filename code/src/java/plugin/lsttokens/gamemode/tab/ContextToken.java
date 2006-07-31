package plugin.lsttokens.gamemode.tab;

import java.util.Map;

import pcgen.core.GameMode;
import pcgen.persistence.lst.TabLoader;
import pcgen.persistence.lst.TabLstToken;
import pcgen.util.enumeration.Tab;

/**
 * Class deals with CONTEXT Token
 */
public class ContextToken implements TabLstToken {

	public String getTokenName() {
		return "CONTEXT";
	}

	public boolean parse(GameMode gameMode, Map<String, String> tab, String value) {
		final Tab aTab = GameMode.getTab(tab.get(TabLoader.TAB));
		gameMode.setTabContext(aTab, value);
		return true;
	}
}

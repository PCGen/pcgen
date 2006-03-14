package plugin.lsttokens.gamemode.tab;

import java.util.Map;

import pcgen.core.GameMode;
import pcgen.persistence.lst.TabLoader;
import pcgen.persistence.lst.TabLstToken;

/**
 * Class deals with CONTEXT Token
 */
public class ContextToken implements TabLstToken {

	public String getTokenName() {
		return "CONTEXT";
	}

	public boolean parse(GameMode gameMode, Map tab, String value) {
		final int tabNum = GameMode.getTabNumber((String)tab.get(TabLoader.TAB));
		gameMode.setTabContext(tabNum, value);
		return true;
	}
}

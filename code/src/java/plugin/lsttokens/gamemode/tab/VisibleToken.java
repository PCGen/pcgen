package plugin.lsttokens.gamemode.tab;

import java.util.Map;

import pcgen.core.GameMode;
import pcgen.persistence.lst.TabLoader;
import pcgen.persistence.lst.TabLstToken;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements TabLstToken {

	public String getTokenName() {
		return "VISIBLE";
	}

	public boolean parse(GameMode gameMode, Map tab, String value) {
		final int tabNum = GameMode.getTabNumber((String)tab.get(TabLoader.TAB));
		gameMode.setTabVisible(tabNum, value.toUpperCase().startsWith("Y"));
		return true;
	}
}

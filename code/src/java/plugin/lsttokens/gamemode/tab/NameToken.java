package plugin.lsttokens.gamemode.tab;

import java.util.Map;

import pcgen.core.GameMode;
import pcgen.persistence.lst.TabLoader;
import pcgen.persistence.lst.TabLstToken;

/**
 * Class deals with NAME Token
 */
public class NameToken implements TabLstToken {

	public String getTokenName() {
		return "NAME";
	}

	public boolean parse(GameMode gameMode, Map tab, String value) {
		final int tabNum = GameMode.getTabNumber((String)tab.get(TabLoader.TAB));
		gameMode.setTabName(tabNum, value);
		return true;
	}
}

package plugin.lsttokens.gamemode.tab;

import java.util.Map;

import pcgen.core.Constants;
import pcgen.core.GameMode;
import pcgen.persistence.lst.TabLoader;
import pcgen.persistence.lst.TabLstToken;
import pcgen.util.Logging;

/**
 * Class deals with TAB Token
 */
public class TabToken implements TabLstToken {

	public String getTokenName() {
		return "TAB";
	}

	public boolean parse(GameMode gameMode, Map tab, String value) {
		tab.put(TabLoader.TAB, value);
		final int tabNum = GameMode.getTabNumber(value);

		if (tabNum == Constants.TAB_INVALID) {
			Logging.errorPrint("TAB name '" + value + "' not a valid tab name. Check pcgen.core.Constants for valid tab names.");
			return false;
		}
		return true;
	}
}

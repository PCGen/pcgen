package plugin.lsttokens.gamemode.tab;

import java.util.Map;

import pcgen.core.GameMode;
import pcgen.persistence.lst.TabLoader;
import pcgen.persistence.lst.TabLstToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Tab;

/**
 * Class deals with TAB Token
 */
public class TabToken implements TabLstToken
{

	public String getTokenName()
	{
		return "TAB";
	}

	public boolean parse(GameMode gameMode, Map<String, String> tab,
		String value)
	{
		tab.put(TabLoader.TAB, value);
		final Tab aTab = GameMode.getTab(value);

		if (aTab == Tab.INVALID)
		{
			Logging
				.errorPrint("TAB name '"
					+ value
					+ "' not a valid tab name. Check pcgen.util.enumeration.Tab for valid tab names.");
			return false;
		}
		return true;
	}
}

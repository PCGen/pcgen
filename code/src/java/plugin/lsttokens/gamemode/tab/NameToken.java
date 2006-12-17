package plugin.lsttokens.gamemode.tab;

import java.util.Map;

import pcgen.core.GameMode;
import pcgen.persistence.lst.TabLoader;
import pcgen.persistence.lst.TabLstToken;
import pcgen.util.enumeration.Tab;

/**
 * Class deals with NAME Token
 */
public class NameToken implements TabLstToken
{

	public String getTokenName()
	{
		return "NAME";
	}

	public boolean parse(GameMode gameMode, Map<String, String> tab,
		String value)
	{
		final Tab aTab = GameMode.getTab(tab.get(TabLoader.TAB));
		gameMode.setTabName(aTab, value);
		return true;
	}
}

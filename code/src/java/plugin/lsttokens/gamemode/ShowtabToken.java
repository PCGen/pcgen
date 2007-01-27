package plugin.lsttokens.gamemode;

import java.net.URI;
import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.util.enumeration.Tab;

/**
 * Class deals with SHOWTAB Token
 */
public class ShowtabToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "SHOWTAB";
	}

	public boolean parse(GameMode gameMode, String value, URI source)
	{
		final StringTokenizer tok = new StringTokenizer(value, "|");
		final String tabName = tok.nextToken();
		final String visibility = tok.nextToken();
		final Tab aTab = GameMode.getTab(tabName);

		if (aTab == Tab.INVALID)
		{
			return false;
		}

		gameMode.setTabVisible(aTab, visibility.toUpperCase().startsWith("Y"));
		return true;
	}
}

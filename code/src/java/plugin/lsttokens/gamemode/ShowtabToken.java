package plugin.lsttokens.gamemode;

import java.net.URI;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.TabInfo;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;

/**
 * Class deals with SHOWTAB Token
 */
public class ShowtabToken implements GameModeLstToken, DeferredToken<TabInfo>,
		DeprecatedToken
{

	public String getTokenName()
	{
		return "SHOWTAB";
	}

	public boolean parse(GameMode gameMode, String value, URI source)
	{
		final StringTokenizer tok = new StringTokenizer(value, "|");
		final String tabName = tok.nextToken();
		Logging.deprecationPrint("This Token is Deprecated. "
				+ "Please use VISIBLE: on the TAB:" + tabName + " line (Source: " + source + ")");
		final String visibility = tok.nextToken();
		CDOMSingleRef<TabInfo> ref = gameMode.getModeContext().ref
				.getCDOMReference(TabInfo.class, tabName);

		boolean set;
		char firstChar = visibility.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (visibility.length() > 1 && !visibility.equalsIgnoreCase("YES"))
			{
				Logging.errorPrint("You should use 'YES' in " + getTokenName()
						+ ": " + value);
				return false;
			}
			set = Boolean.TRUE;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				Logging.errorPrint("You should use 'YES' or 'NO' in "
						+ getTokenName() + ": " + value);
				return false;
			}
			if (visibility.length() > 1 && !visibility.equalsIgnoreCase("NO"))
			{
				Logging.errorPrint("You should use 'YES' or 'NO' in "
						+ getTokenName() + ": " + value);
				return false;
			}
			set = Boolean.FALSE;
		}
		
		gameMode.setTabVisible(ref, set);
		return true;
	}

	public Class<TabInfo> getDeferredTokenClass()
	{
		return TabInfo.class;
	}

	public boolean process(LoadContext context, TabInfo ti)
	{
		Boolean visibility = SettingsHandler.getGame().getTabVisibility(ti);
		if (visibility != null)
		{
			ti.setVisible(visibility);
		}
		return true;
	}

	public String getMessage(CDOMObject obj, String value)
	{
		return "This Token is Deprecated. Please use VISIBLE: on the TAB: line";
	}
}

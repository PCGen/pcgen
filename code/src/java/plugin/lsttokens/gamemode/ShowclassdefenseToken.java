package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SHOWCLASSDEFENSE Token
 */
public class ShowclassdefenseToken implements GameModeLstToken
{

	@Override
	public String getTokenName()
	{
		return "SHOWCLASSDEFENSE";
	}

	@Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setShowClassDefense(value.toUpperCase().startsWith("Y"));
		return true;
	}
}

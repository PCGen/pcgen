package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with DISPLAYVARIABLE2NAME Token
 */
public class Displayvariable2nameToken implements GameModeLstToken
{

	@Override
	public String getTokenName()
	{
		return "DISPLAYVARIABLE2NAME";
	}

	@Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		return true;
	}
}

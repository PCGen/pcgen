package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with DISPLAYVARIABLE2NAME Token
 */
public class Displayvariable2nameToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "DISPLAYVARIABLE2NAME";
	}

	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setVariableDisplay2Name(value);
		return true;
	}
}

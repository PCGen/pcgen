package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with DISPLAYVARIABLE2TEXT Token
 */
public class Displayvariable2textToken implements GameModeLstToken
{

	@Override
	public String getTokenName()
	{
		return "DISPLAYVARIABLE2TEXT";
	}

	@Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setVariableDisplay2Text(value);
		return true;
	}
}

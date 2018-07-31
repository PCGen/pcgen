package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with DISPLAYVARIABLE1NAME Token
 */
public class Displayvariable1nameToken implements GameModeLstToken
{

	@Override
	public String getTokenName()
	{
		return "DISPLAYVARIABLE1NAME";
	}

	@Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setVariableDisplayName(value);
		return true;
	}
}

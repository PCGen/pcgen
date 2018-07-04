package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with DISPLAYVARIABLE1TEXT Token
 */
public class Displayvariable1textToken implements GameModeLstToken
{

	@Override
	public String getTokenName()
	{
		return "DISPLAYVARIABLE1TEXT";
	}

	@Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setVariableDisplayText(value);
		return true;
	}
}

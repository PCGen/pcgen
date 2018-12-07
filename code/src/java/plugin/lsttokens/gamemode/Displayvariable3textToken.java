package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with DISPLAYVARIABLE3TEXT Token
 */
public class Displayvariable3textToken implements GameModeLstToken
{

	@Override
	public String getTokenName()
	{
		return "DISPLAYVARIABLE3TEXT";
	}

	@Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		return true;
	}
}

package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with CLASSTYPE Token
 */
public class ClasstypeToken implements GameModeLstToken
{

    @Override
	public String getTokenName()
	{
		return "CLASSTYPE";
	}

    @Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.addClassType(value);
		return true;
	}
}

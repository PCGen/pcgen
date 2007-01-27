package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with BABABBREV Token
 */
public class BababbrevToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "BABABBREV";
	}

	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setBabAbbrev(value);
		return true;
	}
}

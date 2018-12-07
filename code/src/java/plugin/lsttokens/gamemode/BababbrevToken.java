package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with BABABBREV Token
 */
public class BababbrevToken implements GameModeLstToken
{

	@Override
	public String getTokenName()
	{
		return "BABABBREV";
	}

	@Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		return true;
	}
}

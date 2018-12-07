package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with HPABBREV Token
 */
public class HpabbrevToken implements GameModeLstToken
{

	@Override
	public String getTokenName()
	{
		return "HPABBREV";
	}

	@Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		return true;
	}
}

package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with HPABBREV Token
 */
public class HpabbrevToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "HPABBREV";
	}

	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setHPAbbrev(value);
		return true;
	}
}

package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with HPNAME Token
 */
public class HpnameToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "HPNAME";
	}

	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setHPText(value);
		return true;
	}
}

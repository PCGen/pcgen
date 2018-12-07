package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with HPNAME Token
 */
public class HpnameToken implements GameModeLstToken
{

	@Override
	public String getTokenName()
	{
		return "HPNAME";
	}

	@Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		return true;
	}
}

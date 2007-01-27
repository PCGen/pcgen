package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with DEITY Token
 */
public class DeityToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "DEITY";
	}

	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.addDeityList(value);
		return true;
	}
}

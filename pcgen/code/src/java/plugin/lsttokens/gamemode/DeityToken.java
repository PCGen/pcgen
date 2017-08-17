package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with DEITY Token
 */
public class DeityToken implements GameModeLstToken
{

    @Override
	public String getTokenName()
	{
		return "DEITY";
	}

    @Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.addDeityList(value);
		return true;
	}
}

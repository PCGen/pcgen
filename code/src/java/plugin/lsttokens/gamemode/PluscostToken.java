package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with PLUSCOST Token
 */
public class PluscostToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "PLUSCOST";
	}

	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.addPlusCalculation(value);
		return true;
	}
}

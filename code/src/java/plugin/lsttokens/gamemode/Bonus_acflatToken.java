package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.util.Logging;

/**
 * Class deals with BONUS_ACFLAT Token
 */
public class Bonus_acflatToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "BONUS_ACFLAT";
	}

	public boolean parse(GameMode gameMode, String value, URI source)
	{
		Logging.deprecationPrint(getTokenName() + " is an unused "
				+ "Game Mode token found in " + source.toString() );
		return true;
	}
}

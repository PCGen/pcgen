package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SHORTRANGE Token
 */
public class ShortrangeToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "SHORTRANGE";
	}

	public boolean parse(GameMode gameMode, String value, URI source)
	{
		try
		{
			gameMode.setShortRangeDistance(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}
}

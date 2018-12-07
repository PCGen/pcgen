package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with BABMAXLVL Token
 */
public class BabmaxlvlToken implements GameModeLstToken
{

	@Override
	public String getTokenName()
	{
		return "BABMAXLVL";
	}

	@Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		try
		{
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}
}

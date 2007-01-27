package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with CHECKSMAXLVL Token
 */
public class ChecksmaxlvlToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "CHECKSMAXLVL";
	}

	public boolean parse(GameMode gameMode, String value, URI source)
	{
		try
		{
			gameMode.setChecksMaxLvl(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}
}

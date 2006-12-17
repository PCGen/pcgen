package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with RANGEPENALTY Token
 */
public class RangepenaltyToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "RANGEPENALTY";
	}

	public boolean parse(GameMode gameMode, String value)
	{
		try
		{
			gameMode.setRangePenalty(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}
}

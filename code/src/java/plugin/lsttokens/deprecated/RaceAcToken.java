package plugin.lsttokens.deprecated;

import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with AC Token
 */
public class RaceAcToken implements RaceLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "AC";
	}

	public boolean parse(Race race, String value)
	{
		try
		{
			race.setStartingAC(Integer.valueOf(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}

	public String getMessage(PObject obj, String value)
	{
		return "AC in Race is a non-functioning Token";
	}
}

package plugin.lsttokens.deprecated;

import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with INIT Token
 */
public class InitToken implements RaceLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "INIT";
	}

	public boolean parse(Race race, String value)
	{
		try
		{
			race.setInitMod(Integer.valueOf(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}

	public String getMessage(PObject obj, String value)
	{
		return getTokenName()
			+ " is deprecated, because it does not function (value is never read)";
	}
}

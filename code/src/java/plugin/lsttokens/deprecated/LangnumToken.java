package plugin.lsttokens.deprecated;

import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with LANGNUM Token
 */
public class LangnumToken implements RaceLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "LANGNUM";
	}

	public boolean parse(Race race, String value)
	{
		try
		{
			race.setLangNum(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}

	public String getMessage(PObject obj, String value)
	{
		return "Use BONUS:LANG|BONUS|x instead";
	}
}

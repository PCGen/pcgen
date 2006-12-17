package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with CR Token
 */
public class CrToken implements RaceLstToken
{

	public String getTokenName()
	{
		return "CR";
	}

	public boolean parse(Race race, String value)
	{
		try
		{
			String intValue = value;
			if (intValue.startsWith("1/"))
			{
				intValue = "-" + intValue.substring(2);
			}
			race.setCR(Integer.parseInt(intValue));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}
}

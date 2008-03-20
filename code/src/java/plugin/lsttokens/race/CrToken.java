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
			String floatValue = value;
			if (floatValue.startsWith("1/"))
			{
				float fraction = Float.parseFloat(floatValue.substring(2));
				race.setCR(1 / fraction);
			}
			else
			{
				race.setCR(Float.parseFloat(floatValue));
			}
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}
}

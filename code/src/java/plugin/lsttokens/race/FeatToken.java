package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with FEAT Token
 */
public class FeatToken implements RaceLstToken
{

	public String getTokenName()
	{
		return "FEAT";
	}

	public boolean parse(Race race, String value)
	{
		race.setFeatList(value);
		return true;
	}
}

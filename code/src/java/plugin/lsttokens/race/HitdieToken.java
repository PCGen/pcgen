package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with HITDIE Token
 */
public class HitdieToken implements RaceLstToken
{

	public String getTokenName()
	{
		return "HITDIE";
	}

	public boolean parse(Race race, String value)
	{
		race.setHitDieLock(value);
		return true;
	}
}

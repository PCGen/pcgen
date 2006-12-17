package plugin.lsttokens.race;

import java.util.StringTokenizer;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with MONSTERCLASS Token
 */
public class MonsterclassToken implements RaceLstToken
{

	public String getTokenName()
	{
		return "MONSTERCLASS";
	}

	public boolean parse(Race race, String value)
	{
		try
		{
			final StringTokenizer mclass = new StringTokenizer(value, ":");

			if (mclass.countTokens() != 2)
			{
				return false;
			}
			race.setMonsterClass(mclass.nextToken());
			race.setMonsterClassLevels(Integer.parseInt(mclass.nextToken()));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}
}

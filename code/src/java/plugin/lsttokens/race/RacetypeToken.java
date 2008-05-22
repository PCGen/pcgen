package plugin.lsttokens.race;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with RACETYPE Token
 */
public class RacetypeToken extends AbstractToken implements
		CDOMPrimaryToken<Race>
{

	@Override
	public String getTokenName()
	{
		return "RACETYPE";
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.getObjectContext().put(race, ObjectKey.RACETYPE,
				RaceType.getConstant(value));
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		RaceType raceType = context.getObjectContext().getObject(race,
				ObjectKey.RACETYPE);
		if (raceType == null)
		{
			return null;
		}
		return new String[] { raceType.toString() };
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}
}

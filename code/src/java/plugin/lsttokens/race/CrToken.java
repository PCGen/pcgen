package plugin.lsttokens.race;

import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with CR Token
 */
public class CrToken implements CDOMPrimaryToken<Race>
{

	public String getTokenName()
	{
		return "CR";
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		try
		{
			ChallengeRating cr = new ChallengeRating(value);
			context.getObjectContext().put(race, ObjectKey.CHALLENGE_RATING, cr);
			return true;
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint(getTokenName() + " encountered error: "
					+ e.getLocalizedMessage());
			return false;
		}
	}

	public String[] unparse(LoadContext context, Race race)
	{
		ChallengeRating cr = context.getObjectContext().getObject(race,
				ObjectKey.CHALLENGE_RATING);
		if (cr == null)
		{
			// indicates no Token present
			return null;
		}
		return new String[] { cr.getLSTformat() };
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}
}

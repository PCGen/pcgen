package plugin.lsttokens.race;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with REACH Token
 */
public class ReachToken implements CDOMPrimaryToken<Race>
{

	public String getTokenName()
	{
		return "REACH";
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		try
		{
			Integer i = Integer.valueOf(value);
			if (i.intValue() < 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer >= 0");
				return false;
			}
			context.getObjectContext().put(race, IntegerKey.REACH, i);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Expected an Integer in Tag: " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, Race race)
	{
		Integer reach = context.getObjectContext().getInteger(race,
				IntegerKey.REACH);
		if (reach == null)
		{
			return null;
		}
		if (reach.intValue() < 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { reach.toString() };
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}
}

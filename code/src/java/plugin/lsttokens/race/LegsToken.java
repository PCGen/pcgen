package plugin.lsttokens.race;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with LEGS Token
 */
public class LegsToken implements CDOMPrimaryToken<Race>
{

	public String getTokenName()
	{
		return "LEGS";
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		try
		{
			Integer in = Integer.valueOf(value);
			if (in.intValue() < 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer >= 0");
				return false;
			}
			context.getObjectContext().put(race, IntegerKey.LEGS, in);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
					+ " expected an integer.  Tag must be of the form: "
					+ getTokenName() + ":<int>");
			return false;
		}
	}

	public String[] unparse(LoadContext context, Race race)
	{
		Integer legs = context.getObjectContext().getInteger(race,
				IntegerKey.LEGS);
		if (legs == null)
		{
			return null;
		}
		if (legs.intValue() < 0)
		{
			context
					.addWriteMessage(getTokenName()
							+ " must be an integer >= 0");
			return null;
		}
		return new String[] { legs.toString() };
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}
}

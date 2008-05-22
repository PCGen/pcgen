package plugin.lsttokens.race;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with SKILLMULT Token
 */
public class SkillmultToken implements CDOMPrimaryToken<Race>
{

	public String getTokenName()
	{
		return "SKILLMULT";
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		try
		{
			Integer i = Integer.valueOf(value);
			if (i.intValue() < 0)
			{
				Logging.errorPrint(getTokenName()
						+ " must be an integer greater than or equal to 0");
				return false;
			}
			context.getObjectContext().put(race, IntegerKey.INITIAL_SKILL_MULT,
					i);
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
		Integer sp = context.getObjectContext().getInteger(race,
				IntegerKey.INITIAL_SKILL_MULT);
		if (sp == null)
		{
			return null;
		}
		if (sp.intValue() < 0)
		{
			context
					.addWriteMessage(getTokenName()
							+ " must be an integer >= 0");
			return null;
		}
		return new String[] { sp.toString() };
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}
}

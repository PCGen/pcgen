package plugin.lsttokens.race;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with XTRASKILLPTSPERLVL Token
 */
public class XtraskillptsperlvlToken implements CDOMPrimaryToken<Race>
{

	/**
	 * Get token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "XTRASKILLPTSPERLVL";
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		try
		{
			Integer sp = Integer.valueOf(value);
			if (sp.intValue() <= 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer > 0");
				return false;
			}

			context.getObjectContext().put(race,
					IntegerKey.SKILL_POINTS_PER_LEVEL, sp);
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
				IntegerKey.SKILL_POINTS_PER_LEVEL);
		if (sp == null)
		{
			return null;
		}
		if (sp.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { sp.toString() };
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}
}

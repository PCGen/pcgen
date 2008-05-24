package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with RANGE token
 */
public class RangeToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "RANGE";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			Integer range = Integer.valueOf(value);
			if (range.intValue() < 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
						+ " must be an integer >= 0");
				return false;
			}
			context.getObjectContext().put(eq, IntegerKey.RANGE, range);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " expected an integer.  Tag must be of the form: "
					+ getTokenName() + ":<int>");
			return false;
		}
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		Integer range = context.getObjectContext().getInteger(eq,
				IntegerKey.RANGE);
		if (range == null)
		{
			return null;
		}
		if (range.intValue() < 0)
		{
			context
					.addWriteMessage(getTokenName()
							+ " must be an integer >= 0");
			return null;
		}
		return new String[] { range.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}

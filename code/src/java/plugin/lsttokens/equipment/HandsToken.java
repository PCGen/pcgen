package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with HANDS token
 */
public class HandsToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "HANDS";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			Integer hands = Integer.valueOf(value);
			if (hands.intValue() < 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer > 0");
				return false;
			}
			context.getObjectContext().put(eq, IntegerKey.SLOTS, hands);
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

	public String[] unparse(LoadContext context, Equipment eq)
	{
		Integer hands = context.getObjectContext().getInteger(eq,
				IntegerKey.SLOTS);
		if (hands == null)
		{
			return null;
		}
		if (hands.intValue() < 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { hands.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}

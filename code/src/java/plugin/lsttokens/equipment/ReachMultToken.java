package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with REACHMULT token
 */
public class ReachMultToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "REACHMULT";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			Integer reach = Integer.valueOf(value);
			if (reach.intValue() <= 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer > 0");
				return false;
			}
			context.getObjectContext().put(eq, IntegerKey.REACH_MULT, reach);
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
		Integer mult = context.getObjectContext().getInteger(eq,
				IntegerKey.REACH_MULT);
		if (mult == null)
		{
			return null;
		}
		if (mult.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { mult.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}

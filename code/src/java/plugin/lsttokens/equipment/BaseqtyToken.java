package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with BASEQTY token
 */
public class BaseqtyToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "BASEQTY";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			Integer quan = Integer.valueOf(value);
			if (quan.intValue() <= 0)
			{
				Logging.errorPrint(getTokenName() + " expected an integer > 0");
				return false;
			}
			context.getObjectContext().put(eq, IntegerKey.BASE_QUANTITY, quan);
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
		Integer quantity = context.getObjectContext().getInteger(eq,
				IntegerKey.BASE_QUANTITY);
		if (quantity == null)
		{
			return null;
		}
		if (quantity.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { quantity.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}

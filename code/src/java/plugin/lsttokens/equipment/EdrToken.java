package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with EDR token
 */
public class EdrToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "EDR";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			Integer edr = Integer.valueOf(value);
			if (edr.intValue() <= 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer > 0");
				return false;
			}
			context.getObjectContext().put(eq, IntegerKey.EDR, edr);
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
		Integer edr = context.getObjectContext().getInteger(eq, IntegerKey.EDR);
		if (edr == null)
		{
			return null;
		}
		if (edr.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { edr.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}

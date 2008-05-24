package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with ACCHECK token
 */
public class AccheckToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "ACCHECK";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			Integer acc = Integer.valueOf(value);
			if (acc.intValue() > 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer <= 0");
				return false;
			}
			context.getObjectContext().put(eq, IntegerKey.AC_CHECK, acc);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.deprecationPrint(getTokenName() + " expected an integer.  "
					+ "Assuming zero.  Tag should be of the form: "
					+ getTokenName() + ":<int>");
			context.getObjectContext().put(eq, IntegerKey.AC_CHECK, 0);
			return true;
			// Logging.errorPrint(getTokenName()
			// + " expected an integer. Tag must be of the form: "
			// + getTokenName() + ":<int>");
			// return false;
		}
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		Integer check = context.getObjectContext().getInteger(eq,
				IntegerKey.AC_CHECK);
		if (check == null)
		{
			return null;
		}
		if (check.intValue() > 0)
		{
			context
					.addWriteMessage(getTokenName()
							+ " must be an integer <= 0");
			return null;
		}
		return new String[] { check.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}

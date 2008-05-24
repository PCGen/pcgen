package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with SPELLFAILURE token
 */
public class SpellfailureToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "SPELLFAILURE";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			Integer sf = Integer.valueOf(value);
			if (sf.intValue() <= 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer > 0");
				return false;
			}
			context.getObjectContext().put(eq, IntegerKey.SPELL_FAILURE, sf);
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
		Integer sf = context.getObjectContext().getInteger(eq,
				IntegerKey.SPELL_FAILURE);
		if (sf == null)
		{
			return null;
		}
		if (sf.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { sf.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}

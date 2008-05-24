package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with MAXDEX token
 */
public class MaxdexToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "MAXDEX";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			context.getObjectContext().put(eq, IntegerKey.MAX_DEX,
					Integer.valueOf(value));
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
		Integer maxDexBonus = context.getObjectContext().getInteger(eq,
				IntegerKey.MAX_DEX);
		if (maxDexBonus == null)
		{
			return null;
		}
		return new String[] { maxDexBonus.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}

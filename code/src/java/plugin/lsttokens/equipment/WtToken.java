package plugin.lsttokens.equipment;

import java.math.BigDecimal;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with WT token
 */
public class WtToken implements CDOMPrimaryToken<Equipment>
{

	/**
	 * Get token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "WT";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			BigDecimal weight = new BigDecimal(value);
			if (weight.compareTo(BigDecimal.ZERO) < 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
						+ " was expecting a decimal value >= 0 : " + value);
				return false;
			}
			context.getObjectContext().put(eq, ObjectKey.WEIGHT, weight);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Expected a Double for "
					+ getTokenName() + ": " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		BigDecimal weight = context.getObjectContext().getObject(eq,
				ObjectKey.WEIGHT);
		if (weight == null)
		{
			return null;
		}
		return new String[] { weight.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}

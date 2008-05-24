package plugin.lsttokens.equipment;

import java.math.BigDecimal;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with COST token
 */
public class CostToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "COST";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			BigDecimal cost = new BigDecimal(value);
			// CONSIDER This apparently is not a requirement, since some items
			// in the RSRD have negative COST?
			//
			// if (cost.compareTo(BigDecimal.ZERO) < 0)
			// {
			// Logging.errorPrint(getTokenName()
			// + " must be a positive number: " + value);
			// return false;
			// }
			context.getObjectContext().put(eq, ObjectKey.COST, cost);
			return true;
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint(getTokenName() + " expected a number: " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		BigDecimal bd = context.getObjectContext()
				.getObject(eq, ObjectKey.COST);
		if (bd == null)
		{
			return null;
		}
		return new String[] { bd.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}

package plugin.lsttokens.subclass;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.SubClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with PROHIBITCOST Token
 */
public class ProhibitcostToken implements CDOMPrimaryToken<SubClass>
{

	public String getTokenName()
	{
		return "PROHIBITCOST";
	}

	public boolean parse(LoadContext context, SubClass sc, String value)
			throws PersistenceLayerException
	{
		try
		{
			Integer cost = Integer.valueOf(value);
			if (cost.intValue() < 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer >= 0");
				return false;
			}
			context.getObjectContext().put(sc, IntegerKey.PROHIBIT_COST, cost);
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

	public String[] unparse(LoadContext context, SubClass sc)
	{
		Integer cost = context.getObjectContext().getInteger(sc,
				IntegerKey.PROHIBIT_COST);
		if (cost == null)
		{
			return null;
		}
		if (cost.intValue() < 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { cost.toString() };
	}

	public Class<SubClass> getTokenClass()
	{
		return SubClass.class;
	}
}

package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with MAXLEVEL Token
 */
public class MaxlevelToken implements CDOMPrimaryToken<PCClass>
{

	public String getTokenName()
	{
		return "MAXLEVEL";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		Integer lim;
		if ("NOLIMIT".equalsIgnoreCase(value))
		{
			lim = PCClass.NO_LEVEL_LIMIT;
		}
		else
		{
			try
			{
				lim = Integer.valueOf(value);
				if (lim.intValue() <= 0)
				{
					Logging.errorPrint("Value less than 1 is not valid for "
							+ getTokenName() + ": " + value);
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Value was not a number for "
						+ getTokenName() + ": " + value);
				return false;
			}
		}
		context.getObjectContext().put(pcc, IntegerKey.LEVEL_LIMIT, lim);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Integer lim = context.getObjectContext().getInteger(pcc,
				IntegerKey.LEVEL_LIMIT);
		if (lim == null)
		{
			return null;
		}
		String returnString = lim.toString();
		if (lim.equals(PCClass.NO_LEVEL_LIMIT))
		{
			returnString = "NOLIMIT";
		}
		else if (lim.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { returnString };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}

}

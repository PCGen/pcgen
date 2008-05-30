package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with LEVELSPERFEAT Token
 */
public class LevelsperfeatToken implements CDOMPrimaryToken<PCClass>
{

	public String getTokenName()
	{
		return "LEVELSPERFEAT";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		try
		{
			Integer in = Integer.valueOf(value);
			if (in.intValue() < 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer >= 0");
				return false;
			}
			context.getObjectContext().put(pcc, IntegerKey.LEVELS_PER_FEAT, in);
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

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Integer lpf = context.getObjectContext().getInteger(pcc,
				IntegerKey.LEVELS_PER_FEAT);
		if (lpf == null)
		{
			return null;
		}
		if (lpf.intValue() < 0)
		{
			context
					.addWriteMessage(getTokenName()
							+ " must be an integer >= 0");
			return null;
		}
		return new String[] { lpf.toString() };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}

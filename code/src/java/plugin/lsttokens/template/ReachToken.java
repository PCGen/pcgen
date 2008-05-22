package plugin.lsttokens.template;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with REACH Token
 */
public class ReachToken implements CDOMPrimaryToken<PCTemplate>
{

	public String getTokenName()
	{
		return "REACH";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		try
		{
			Integer i = Integer.valueOf(value);
			if (i.intValue() < 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer >= 0");
				return false;
			}
			context.getObjectContext().put(template, IntegerKey.REACH, i);
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

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Integer reach =
				context.getObjectContext().getInteger(pct, IntegerKey.REACH);
		if (reach == null)
		{
			return null;
		}
		if (reach.intValue() < 0)
		{
			context
				.addWriteMessage(getTokenName() + " must be an integer >= 0");
			return null;
		}
		return new String[]{reach.toString()};
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}

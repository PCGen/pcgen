package plugin.lsttokens.template;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with HANDS Token
 */
public class HandsToken implements CDOMPrimaryToken<PCTemplate>
{

	public String getTokenName()
	{
		return "HANDS";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		try
		{
			Integer in = Integer.valueOf(value);
			if (in.intValue() < 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer >= 0");
				return false;
			}
			context.getObjectContext().put(template, IntegerKey.HANDS, in);
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
		Integer hands =
				context.getObjectContext().getInteger(pct, IntegerKey.HANDS);
		if (hands == null)
		{
			return null;
		}
		if (hands.intValue() < 0)
		{
			context
				.addWriteMessage(getTokenName() + " must be an integer >= 0");
			return null;
		}
		return new String[]{hands.toString()};
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}

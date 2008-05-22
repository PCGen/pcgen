package plugin.lsttokens.template;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with NONPP Token
 */
public class NonppToken implements CDOMPrimaryToken<PCTemplate>
{

	public String getTokenName()
	{
		return "NONPP";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		try
		{
			Integer nonpp = Integer.valueOf(value);
			if (nonpp.intValue() > 0)
			{
				Logging.errorPrint("Non-Proficiency Penalty must be "
						+ "less than or equal to zero: " + value);
				return false;
			}
			context.getObjectContext().put(template, IntegerKey.NONPP, nonpp);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Non-Proficiency Penalty must be a number "
					+ "less than or equal to zero: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Integer nonpp = context.getObjectContext().getInteger(pct,
				IntegerKey.NONPP);
		if (nonpp == null)
		{
			return null;
		}
		if (nonpp.intValue() > 0)
		{
			context.addWriteMessage("Non-Proficiency Penalty must be "
					+ "less than or equal to zero: " + nonpp);
			return null;
		}
		return new String[] { nonpp.toString() };
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}

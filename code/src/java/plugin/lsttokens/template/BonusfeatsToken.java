package plugin.lsttokens.template;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with BONUSFEATS Token
 */
public class BonusfeatsToken implements CDOMPrimaryToken<PCTemplate>
{

	public String getTokenName()
	{
		return "BONUSFEATS";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		try
		{
			int featCount = Integer.parseInt(value);
			if (featCount <= 0)
			{
				Logging.errorPrint("Invalid integer in " + getTokenName()
						+ ": must be greater than zero");
				return false;
			}
			context.getObjectContext().put(template, IntegerKey.BONUS_FEATS,
					featCount);
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
		Integer featCount = context.getObjectContext().getInteger(pct,
				IntegerKey.BONUS_FEATS);
		if (featCount == null)
		{
			return null;
		}
		if (featCount.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { featCount.toString() };
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}

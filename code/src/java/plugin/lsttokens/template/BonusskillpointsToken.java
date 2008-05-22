package plugin.lsttokens.template;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with BONUSSKILLPOINTS Token
 */
public class BonusskillpointsToken implements CDOMPrimaryToken<PCTemplate>
{

	public String getTokenName()
	{
		return "BONUSSKILLPOINTS";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		try
		{
			int skillCount = Integer.parseInt(value);
			if (skillCount <= 0)
			{
				Logging.errorPrint(getTokenName()
						+ " must be an integer greater than zero");
				return false;
			}
			context.getObjectContext().put(template,
					IntegerKey.BONUS_CLASS_SKILL_POINTS, skillCount);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid Number in " + getTokenName() + ": "
					+ value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Integer points = context.getObjectContext().getInteger(pct,
				IntegerKey.BONUS_CLASS_SKILL_POINTS);
		if (points == null)
		{
			return null;
		}
		if (points.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { points.toString() };
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}

package plugin.lsttokens.statsandchecks.stat;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with STATMOD Token
 */
public class StatmodToken implements CDOMPrimaryToken<PCStat>
{

	public String getTokenName()
	{
		return "STATMOD";
	}

	public boolean parse(LoadContext context, PCStat stat, String value)
	{
		if (value == null || value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		context.getObjectContext().put(stat, StringKey.STAT_MOD, value);
		return true;
	}

	public String[] unparse(LoadContext context, PCStat stat)
	{
		String target = context.getObjectContext().getString(stat,
				StringKey.STAT_MOD);
		if (target == null)
		{
			return null;
		}
		return new String[] { target };
	}

	public Class<PCStat> getTokenClass()
	{
		return PCStat.class;
	}
}

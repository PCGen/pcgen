package plugin.lsttokens.campaign;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with HELP Token
 */
public class HelpToken implements CDOMPrimaryToken<Campaign>
{

	public String getTokenName()
	{
		return "HELP";
	}

	public boolean parse(LoadContext context, Campaign camp, String value)
	{
		if (value == null || value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		context.getObjectContext().put(camp, StringKey.HELP, value);
		return true;
	}

	public String[] unparse(LoadContext context, Campaign camp)
	{
		String help =
				context.getObjectContext().getString(camp, StringKey.HELP);
		if (help == null)
		{
			return null;
		}
		return new String[]{help};
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}
}

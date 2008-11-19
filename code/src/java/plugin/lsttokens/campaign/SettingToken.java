package plugin.lsttokens.campaign;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with SETTING Token
 */
public class SettingToken implements CDOMPrimaryToken<Campaign>
{

	public String getTokenName()
	{
		return "SETTING";
	}

	public boolean parse(LoadContext context, Campaign camp, String value)
	{
		if (value == null || value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		context.getObjectContext().put(camp, StringKey.SETTING, value);
		return true;
	}

	public String[] unparse(LoadContext context, Campaign camp)
	{
		String setting =
				context.getObjectContext().getString(camp, StringKey.SETTING);
		if (setting == null)
		{
			return null;
		}
		return new String[]{setting};
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}
}

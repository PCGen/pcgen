package plugin.lsttokens.campaign;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with GENRE Token
 */
public class GenreToken implements CDOMPrimaryToken<Campaign>
{

	public String getTokenName()
	{
		return "GENRE";
	}

	public boolean parse(LoadContext context, Campaign camp, String value)
	{
		if (value == null || value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		context.getObjectContext().put(camp, StringKey.GENRE, value);
		return true;
	}

	public String[] unparse(LoadContext context, Campaign camp)
	{
		String genre =
				context.getObjectContext().getString(camp, StringKey.GENRE);
		if (genre == null)
		{
			return null;
		}
		return new String[]{genre};
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}}

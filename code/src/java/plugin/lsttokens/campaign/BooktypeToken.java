package plugin.lsttokens.campaign;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with BOOKTYPE Token
 */
public class BooktypeToken implements CDOMPrimaryToken<Campaign>
{

	public String getTokenName()
	{
		return "BOOKTYPE";
	}

	public boolean parse(LoadContext context, Campaign camp, String value)
	{
		if (value == null || value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		context.getObjectContext().put(camp, StringKey.BOOK_TYPE, value);
		return true;
	}

	public String[] unparse(LoadContext context, Campaign camp)
	{
		String booktype =
				context.getObjectContext().getString(camp, StringKey.BOOK_TYPE);
		if (booktype == null)
		{
			return null;
		}
		return new String[]{booktype};
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}
}

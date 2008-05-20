package plugin.lsttokens.deity;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Deity;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with TITLE Token
 */
public class TitleToken implements CDOMPrimaryToken<Deity>
{

	public String getTokenName()
	{
		return "TITLE";
	}

	public boolean parse(LoadContext context, Deity deity, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		context.getObjectContext().put(deity, StringKey.TITLE, value);
		return true;
	}

	public String[] unparse(LoadContext context, Deity deity)
	{
		String title =
				context.getObjectContext().getString(deity, StringKey.TITLE);
		if (title == null)
		{
			return null;
		}
		return new String[]{title};
	}

	public Class<Deity> getTokenClass()
	{
		return Deity.class;
	}
}

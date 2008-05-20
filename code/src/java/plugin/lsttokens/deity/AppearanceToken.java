package plugin.lsttokens.deity;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Deity;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with APPEARANCE Token
 */
public class AppearanceToken implements CDOMPrimaryToken<Deity>
{

	public String getTokenName()
	{
		return "APPEARANCE";
	}

	public boolean parse(LoadContext context, Deity deity, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		context.getObjectContext().put(deity, StringKey.APPEARANCE, value);
		return true;
	}

	public String[] unparse(LoadContext context, Deity deity)
	{
		String appearance =
				context.getObjectContext().getString(deity,
					StringKey.APPEARANCE);
		if (appearance == null)
		{
			return null;
		}
		return new String[]{appearance};
	}

	public Class<Deity> getTokenClass()
	{
		return Deity.class;
	}
}

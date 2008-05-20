package plugin.lsttokens.deity;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Deity;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with SYMBOL Token
 */
public class SymbolToken implements CDOMPrimaryToken<Deity>
{

	public String getTokenName()
	{
		return "SYMBOL";
	}

	public boolean parse(LoadContext context, Deity deity, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		context.getObjectContext().put(deity, StringKey.HOLY_ITEM, value);
		return true;
	}

	public String[] unparse(LoadContext context, Deity deity)
	{
		String holyItem =
				context.getObjectContext()
					.getString(deity, StringKey.HOLY_ITEM);
		if (holyItem == null)
		{
			return null;
		}
		return new String[]{holyItem};
	}

	public Class<Deity> getTokenClass()
	{
		return Deity.class;
	}
}

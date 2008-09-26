package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with ITEMCREATE Token
 */
public class ItemcreateToken implements CDOMPrimaryToken<PCClass>
{

	public String getTokenName()
	{
		return "ITEMCREATE";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		context.getObjectContext().put(pcc, StringKey.ITEMCREATE, value);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		String title =
				context.getObjectContext().getString(pcc, StringKey.ITEMCREATE);
		if (title == null)
		{
			return null;
		}
		return new String[]{title};
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}

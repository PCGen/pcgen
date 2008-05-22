package plugin.lsttokens.template;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with REMOVABLE Token
 */
public class RemovableToken implements CDOMPrimaryToken<PCTemplate>
{

	public String getTokenName()
	{
		return "REMOVABLE";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		Boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.errorPrint("You should use 'YES' as the "
					+ getTokenName() + ": " + value);
				return false;
			}
			set = Boolean.TRUE;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
				return false;
			}
			if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
				return false;
			}
			set = Boolean.FALSE;
		}
		context.getObjectContext().put(template, ObjectKey.REMOVABLE, set);
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Boolean b =
				context.getObjectContext().getObject(pct, ObjectKey.REMOVABLE);
		if (b == null)
		{
			return null;
		}
		return new String[]{b.booleanValue() ? "YES" : "NO"};
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}

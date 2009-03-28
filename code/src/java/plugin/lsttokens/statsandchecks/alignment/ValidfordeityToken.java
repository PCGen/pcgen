package plugin.lsttokens.statsandchecks.alignment;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCAlignment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with VALIDFORDEITY Token
 */
public class ValidfordeityToken extends AbstractToken implements
		CDOMPrimaryToken<PCAlignment>
{

	@Override
	public String getTokenName()
	{
		return "VALIDFORDEITY";
	}

	public boolean parse(LoadContext context, PCAlignment al, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
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
		context.getObjectContext().put(al, ObjectKey.VALID_FOR_DEITY, set);
		return true;
	}

	public String[] unparse(LoadContext context, PCAlignment al)
	{
		Boolean b = context.getObjectContext().getObject(al,
				ObjectKey.VALID_FOR_DEITY);
		if (b == null)
		{
			return null;
		}
		return new String[] { b.booleanValue() ? "YES" : "NO" };
	}

	public Class<PCAlignment> getTokenClass()
	{
		return PCAlignment.class;
	}
}

package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author joe.frazier
 * 
 * Added for [ 1849571 ] New Class tag: ALLOWBASECLASS:x
 * 
 */
public class AllowBaseClassToken implements CDOMPrimaryToken<PCClass>
{
	public String getTokenName()
	{
		return "ALLOWBASECLASS";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
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
		context.getObjectContext().put(pcc, ObjectKey.ALLOWBASECLASS, set);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Boolean sb = context.getObjectContext().getObject(pcc,
				ObjectKey.ALLOWBASECLASS);
		if (sb == null)
		{
			return null;
		}
		return new String[] { sb.booleanValue() ? "YES" : "NO" };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}

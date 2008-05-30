package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with MODTOSKILLS Token
 */
public class ModtoskillsToken implements CDOMPrimaryToken<PCClass>
{

	public String getTokenName()
	{
		return "MODTOSKILLS";
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
		context.getObjectContext().put(pcc, ObjectKey.MOD_TO_SKILLS, set);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Boolean mts = context.getObjectContext().getObject(pcc,
				ObjectKey.MOD_TO_SKILLS);
		if (mts == null)
		{
			return null;
		}
		return new String[] { mts.booleanValue() ? "YES" : "NO" };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}

package plugin.lsttokens.ability;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with the MULT token
 */
public class MultToken implements CDOMPrimaryToken<Ability>
{

	public String getTokenName()
	{
		return "MULT";
	}

	public boolean parse(LoadContext context, Ability ability, String value)
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
		context.getObjectContext()
				.put(ability, ObjectKey.MULTIPLE_ALLOWED, set);
		return true;
	}

	public String[] unparse(LoadContext context, Ability ability)
	{
		Boolean mult = context.getObjectContext().getObject(ability,
				ObjectKey.MULTIPLE_ALLOWED);
		if (mult == null)
		{
			return null;
		}
		return new String[] { mult.booleanValue() ? "YES" : "NO" };
	}

	public Class<Ability> getTokenClass()
	{
		return Ability.class;
	}
}

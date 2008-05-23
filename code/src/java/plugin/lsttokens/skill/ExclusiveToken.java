package plugin.lsttokens.skill;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Skill;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with EXCLUSIVE Token
 */
public class ExclusiveToken implements CDOMPrimaryToken<Skill>
{

	public String getTokenName()
	{
		return "EXCLUSIVE";
	}

	public boolean parse(LoadContext context, Skill skill, String value)
	{
		boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName());
				return false;
			}
			set = true;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n'
				&& !value.equalsIgnoreCase("NO"))
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName());
				return false;
			}
			set = false;
		}
		context.getObjectContext().put(skill, ObjectKey.EXCLUSIVE, set);
		return true;
	}

	public String[] unparse(LoadContext context, Skill skill)
	{
		Boolean exclusive =
				context.getObjectContext()
					.getObject(skill, ObjectKey.EXCLUSIVE);
		if (exclusive == null)
		{
			return null;
		}
		return new String[]{exclusive.booleanValue() ? "YES" : "NO"};
	}

	public Class<Skill> getTokenClass()
	{
		return Skill.class;
	}
}

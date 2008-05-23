package plugin.lsttokens.skill;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Skill;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with USEUNTRAINED Token
 */
public class UseuntrainedToken implements CDOMPrimaryToken<Skill>
{

	public String getTokenName()
	{
		return "USEUNTRAINED";
	}

	public boolean parse(LoadContext context, Skill skill, String value)
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
		context.getObjectContext().put(skill, ObjectKey.USE_UNTRAINED, set);
		return true;
	}

	public String[] unparse(LoadContext context, Skill skill)
	{
		Boolean useUntrained =
				context.getObjectContext().getObject(skill,
					ObjectKey.USE_UNTRAINED);
		if (useUntrained == null)
		{
			return null;
		}
		return new String[]{useUntrained.booleanValue() ? "YES" : "NO"};
	}

	public Class<Skill> getTokenClass()
	{
		return Skill.class;
	}
}

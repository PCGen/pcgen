package plugin.lsttokens.skill;

import pcgen.core.Skill;
import pcgen.persistence.lst.SkillLstToken;
import pcgen.util.Logging;

/**
 * Class deals with EXCLUSIVE Token
 */
public class ExclusiveToken implements SkillLstToken
{

	public String getTokenName()
	{
		return "EXCLUSIVE";
	}

	public boolean parse(Skill skill, String value)
	{
		boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.deprecationPrint("You should use 'YES' or 'NO' as the "
					+ getTokenName());
				Logging
					.deprecationPrint("Abbreviations will fail after PCGen 5.14");
			}
			set = true;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n'
				&& !value.equalsIgnoreCase("NO"))
			{
				Logging.deprecationPrint("You should use 'YES' or 'NO' as the "
					+ getTokenName());
				Logging
					.deprecationPrint("Abbreviations will fail after PCGen 5.14");
			}
			set = false;
		}
		skill.setIsExclusive(set);
		return true;
	}
}

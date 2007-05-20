package plugin.lsttokens.skill;

import pcgen.core.Skill;
import pcgen.persistence.lst.SkillLstToken;

/**
 * Class deals with USEUNTRAINED Token
 */
public class UseuntrainedToken implements SkillLstToken
{

	public String getTokenName()
	{
		return "USEUNTRAINED";
	}

	public boolean parse(Skill skill, String value)
	{
		boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				// 514 abbreviation cleanup
//				Logging.errorPrint("You should use 'YES' or 'NO' as the "
//					+ getTokenName());
//				Logging.errorPrint("Strange Abbreviations will fail after PCGen 5.12");
			}
			set = true;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n'
				&& !value.equalsIgnoreCase("NO"))
			{
				// 514 abbreviation cleanup
//				Logging.errorPrint("You should use 'YES' or 'NO' as the "
//					+ getTokenName());
//				Logging.errorPrint("Strange Abbreviations will fail after PCGen 5.12");
			}
			set = false;
		}
		skill.setUntrained(set);
		return true;
	}
}

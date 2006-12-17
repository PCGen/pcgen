package plugin.lsttokens.skill;

import pcgen.core.Skill;
import pcgen.persistence.lst.SkillLstToken;

/**
 * Class deals with CLASSES Token
 */
public class ClassesToken implements SkillLstToken
{

	public String getTokenName()
	{
		return "CLASSES";
	}

	public boolean parse(Skill skill, String value)
	{
		skill.addClassList(value);
		return true;
	}
}

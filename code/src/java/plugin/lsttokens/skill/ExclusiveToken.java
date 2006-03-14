package plugin.lsttokens.skill;

import pcgen.core.Skill;
import pcgen.persistence.lst.SkillLstToken;

/**
 * Class deals with EXCLUSIVE Token
 */
public class ExclusiveToken implements SkillLstToken {

	public String getTokenName() {
		return "EXCLUSIVE";
	}

	public boolean parse(Skill skill, String value) {
		skill.setIsExclusive(value.startsWith("Y"));
		return true;
	}
}

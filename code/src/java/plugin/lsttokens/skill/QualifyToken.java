package plugin.lsttokens.skill;

import pcgen.core.Skill;
import pcgen.persistence.lst.SkillLstToken;

/**
 * Class deals with QUALIFY Token
 */
public class QualifyToken implements SkillLstToken {

	public String getTokenName() {
		return "QUALIFY";
	}

	public boolean parse(Skill skill, String value) {
		skill.setQualifyString(value);
		return true;
	}
}

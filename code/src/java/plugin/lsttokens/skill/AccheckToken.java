package plugin.lsttokens.skill;

import pcgen.core.Skill;
import pcgen.persistence.lst.SkillLstToken;

/**
 * Class deals with ACHECK Token
 */
public class AccheckToken implements SkillLstToken {

	public String getTokenName() {
		return "ACHECK";
	}

	public boolean parse(Skill skill, String value) {
		skill.setACheck(value);
		return true;
	}
}

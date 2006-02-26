package plugin.lsttokens.skill;

import pcgen.core.Skill;
import pcgen.persistence.lst.SkillLstToken;

/**
 * Class deals with KEYSTAT Token
 */
public class KeystatToken implements SkillLstToken {

	public String getTokenName() {
		return "KEYSTAT";
	}

	public boolean parse(Skill skill, String value) {
		skill.setKeyStat(value);
		return true;
	}
}

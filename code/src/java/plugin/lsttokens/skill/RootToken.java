package plugin.lsttokens.skill;

import pcgen.core.Skill;
import pcgen.persistence.lst.SkillLstToken;

/**
 * Class deals with ROOT Token
 */
public class RootToken implements SkillLstToken {

	public String getTokenName() {
		return "ROOT";
	}

	public boolean parse(Skill skill, String value) {
		skill.setRootName(value);
		return true;
	}
}

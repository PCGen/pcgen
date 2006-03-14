package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with SKILL Token
 */
public class SkillToken implements RaceLstToken {

	public String getTokenName() {
		return "SKILL";
	}

	public boolean parse(Race race, String value) {
		race.setBonusSkillList(value);
		return true;
	}
}

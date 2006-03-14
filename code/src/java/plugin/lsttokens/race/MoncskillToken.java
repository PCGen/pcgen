package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with MONCSKILL Token
 */
public class MoncskillToken implements RaceLstToken {

	public String getTokenName() {
		return "MONCSKILL";
	}

	public boolean parse(Race race, String value) {
		race.setMonCSkillList(value);
		return true;
	}
}

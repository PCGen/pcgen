package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with MONCCSKILL Token
 */
public class MonccskillToken implements RaceLstToken {

	public String getTokenName() {
		return "MONCCSKILL";
	}

	public boolean parse(Race race, String value) {
		race.setMonCCSkillList(value);
		return true;
	}
}

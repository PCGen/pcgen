package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with PROF Token
 */
public class ProfToken implements RaceLstToken {

	public String getTokenName() {
		return "PROF";
	}

	public boolean parse(Race race, String value) {
		race.setWeaponProfs(value);
		return true;
	}
}

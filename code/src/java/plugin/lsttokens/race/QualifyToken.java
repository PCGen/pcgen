package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with QUALIFY Token
 */
public class QualifyToken implements RaceLstToken {

	public String getTokenName() {
		return "QUALIFY";
	}

	public boolean parse(Race race, String value) {
		race.setQualifyString(value);
		return true;
	}
}

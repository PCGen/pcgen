package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with RACENAME Token
 */
public class RacenameToken implements RaceLstToken {

	public String getTokenName() {
		return "RACENAME";
	}

	public boolean parse(Race race, String value) {
		race.setDisplayName(value);
		return true;
	}
}

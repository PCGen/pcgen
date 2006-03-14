package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with RACETYPE Token
 */
public class RacetypeToken implements RaceLstToken {

	public String getTokenName() {
		return "RACETYPE";
	}

	public boolean parse(Race race, String value) {
		race.setRaceType(value);
		return true;
	}
}

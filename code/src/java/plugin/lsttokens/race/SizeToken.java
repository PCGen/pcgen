package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with SIZE Token
 */
public class SizeToken implements RaceLstToken {

	public String getTokenName() {
		return "SIZE";
	}

	public boolean parse(Race race, String value) {
		race.setSize(value);
		return true;
	}
}

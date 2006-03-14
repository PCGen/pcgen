package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with BAB Token
 */
public class BabToken implements RaceLstToken {

	public String getTokenName() {
		return "BAB";
	}

	public boolean parse(Race race, String value) {
		try {
			race.setBAB(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

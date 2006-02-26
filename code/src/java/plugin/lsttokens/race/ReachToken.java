package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with REACH Token
 */
public class ReachToken implements RaceLstToken {

	public String getTokenName() {
		return "REACH";
	}

	public boolean parse(Race race, String value) {
		try {
			race.setReach(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

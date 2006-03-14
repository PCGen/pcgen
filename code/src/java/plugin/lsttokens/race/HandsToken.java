package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with HANDS Token
 */
public class HandsToken implements RaceLstToken {

	public String getTokenName() {
		return "HANDS";
	}

	public boolean parse(Race race, String value) {
		try {
			race.setHands(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

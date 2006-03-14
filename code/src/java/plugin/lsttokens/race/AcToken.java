package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with AC Token
 */
public class AcToken implements RaceLstToken {

	public String getTokenName() {
		return "AC";
	}

	public boolean parse(Race race, String value) {
		try {
			race.setStartingAC(new Integer(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

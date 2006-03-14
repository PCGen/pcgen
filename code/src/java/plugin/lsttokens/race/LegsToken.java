package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with LEGS Token
 */
public class LegsToken implements RaceLstToken {

	public String getTokenName() {
		return "LEGS";
	}

	public boolean parse(Race race, String value) {
		try {
			race.setLegs(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

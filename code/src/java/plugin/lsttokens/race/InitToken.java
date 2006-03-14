package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with INIT Token
 */
public class InitToken implements RaceLstToken {

	public String getTokenName() {
		return "INIT";
	}

	public boolean parse(Race race, String value) {
		try {
			race.setInitMod(new Integer(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

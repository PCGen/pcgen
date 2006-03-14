package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with CR Token
 */
public class CrToken implements RaceLstToken {

	public String getTokenName() {
		return "CR";
	}

	public boolean parse(Race race, String value) {
		try {
			if (value.startsWith("1/")) {
				value = "-" + value.substring(2);
			}
			race.setCR(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

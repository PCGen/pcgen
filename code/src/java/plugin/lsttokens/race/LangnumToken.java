package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with LANGNUM Token
 */
public class LangnumToken implements RaceLstToken {

	public String getTokenName() {
		return "LANGNUM";
	}

	public boolean parse(Race race, String value) {
		try {
			race.setLangNum(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

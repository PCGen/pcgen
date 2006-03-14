package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with STARTFEATS Token
 */
public class StartfeatsToken implements RaceLstToken {

	public String getTokenName() {
		return "STARTFEATS";
	}

	public boolean parse(Race race, String value) {
		try {
			race.setBonusInitialFeats(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

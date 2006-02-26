package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with SKILLMULT Token
 */
public class SkillmultToken implements RaceLstToken {

	public String getTokenName() {
		return "SKILLMULT";
	}

	public boolean parse(Race race, String value) {
		try {
			race.setInitialSkillMultiplier(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

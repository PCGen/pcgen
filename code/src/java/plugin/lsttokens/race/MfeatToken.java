package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with MFEAT Token
 */
public class MfeatToken implements RaceLstToken {

	public String getTokenName() {
		return "MFEAT";
	}

	public boolean parse(Race race, String value) {
		race.setMFeatList(value);
		return true;
	}
}

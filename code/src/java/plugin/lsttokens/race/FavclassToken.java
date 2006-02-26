package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with FAVCLASS Token
 */
public class FavclassToken implements RaceLstToken {

	public String getTokenName() {
		return "FAVCLASS";
	}

	public boolean parse(Race race, String value) {
		race.setFavoredClass(value);
		return true;
	}
}

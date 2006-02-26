package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with LEVELADJUSTMENT Token
 */
public class LeveladjustmentToken implements RaceLstToken {

	public String getTokenName() {
		return "LEVELADJUSTMENT";
	}

	public boolean parse(Race race, String value) {
		race.setLevelAdjustment(value);
		return true;
	}
}

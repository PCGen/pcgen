package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with LANGBONUS Token
 */
public class LangbonusToken implements RaceLstToken {

	public String getTokenName() {
		return "LANGBONUS";
	}

	public boolean parse(Race race, String value) {
		race.setLanguageBonus(value);
		return true;
	}
}

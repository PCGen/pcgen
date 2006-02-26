package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with XTRASKILLPTSPERLVL Token
 */
public class XtraskillptsperlvlToken implements RaceLstToken {

	public String getTokenName() {
		return "XTRASKILLPTSPERLVL";
	}

	public boolean parse(Race race, String value) {
		try {
			race.setBonusSkillsPerLevel(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

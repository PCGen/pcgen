package plugin.lsttokens.statsandchecks.bonusspell;

import java.util.Map;

import pcgen.persistence.lst.BonusSpellLoader;
import pcgen.persistence.lst.BonusSpellLstToken;

/**
 * Class deals with BASESTATSCORE Token
 */
public class BasestatscoreToken implements BonusSpellLstToken {

	public String getTokenName() {
		return "BASESTATSCORE";
	}

	public boolean parse(Map bonus, String value) {
		bonus.put(BonusSpellLoader.BASE_STAT_SCORE, value);
		return true;
	}
}

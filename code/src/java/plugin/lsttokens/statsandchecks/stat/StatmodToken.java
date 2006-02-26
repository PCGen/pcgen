package plugin.lsttokens.statsandchecks.stat;

import pcgen.core.PCStat;
import pcgen.persistence.lst.PCStatLstToken;

/**
 * Class deals with STATMOD Token
 */
public class StatmodToken implements PCStatLstToken {

	public String getTokenName() {
		return "STATMOD";
	}

	public boolean parse(PCStat stat, String value) {
		stat.setStatMod(value);
		return true;
	}
}

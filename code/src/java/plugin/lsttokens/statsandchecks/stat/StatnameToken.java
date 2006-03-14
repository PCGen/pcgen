package plugin.lsttokens.statsandchecks.stat;

import pcgen.core.PCStat;
import pcgen.persistence.lst.PCStatLstToken;

/**
 * Class deals with STATNAME Token
 */
public class StatnameToken implements PCStatLstToken {

	public String getTokenName() {
		return "STATNAME";
	}

	public boolean parse(PCStat stat, String value) {
		stat.setName(value);
		return true;
	}
}

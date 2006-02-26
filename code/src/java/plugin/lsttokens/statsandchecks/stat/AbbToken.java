package plugin.lsttokens.statsandchecks.stat;

import pcgen.core.PCStat;
import pcgen.persistence.lst.PCStatLstToken;

/**
 * Class deals with ABB Token
 */
public class AbbToken implements PCStatLstToken {

	public String getTokenName() {
		return "ABB";
	}

	public boolean parse(PCStat stat, String value) {
		stat.setAbb(value);
		return true;
	}
}

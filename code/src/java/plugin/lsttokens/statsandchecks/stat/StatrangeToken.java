package plugin.lsttokens.statsandchecks.stat;

import pcgen.core.PCStat;
import pcgen.persistence.lst.PCStatLstToken;

/**
 * Class deals with STATRANGE Token
 */
public class StatrangeToken implements PCStatLstToken {

	public String getTokenName() {
		return "STATRANGE";
	}

	public boolean parse(PCStat stat, String value) {
		stat.setStatRange(value);
		return true;
	}
}

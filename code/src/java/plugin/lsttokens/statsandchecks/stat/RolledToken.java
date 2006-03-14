package plugin.lsttokens.statsandchecks.stat;

import pcgen.core.PCStat;
import pcgen.persistence.lst.PCStatLstToken;

/**
 * Class deals with PENALTYVAR Token
 */
public class RolledToken implements PCStatLstToken {

	public String getTokenName() {
		return "ROLLED";
	}

	public boolean parse(PCStat stat, String value) {
		stat.setRolled(value.toUpperCase().startsWith("Y"));
		return true;
	}
}

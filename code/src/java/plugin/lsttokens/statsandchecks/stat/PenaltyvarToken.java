package plugin.lsttokens.statsandchecks.stat;

import pcgen.core.PCStat;
import pcgen.persistence.lst.PCStatLstToken;

/**
 * Class deals with PENALTYVAR Token
 */
public class PenaltyvarToken implements PCStatLstToken {

	public String getTokenName() {
		return "PENALTYVAR";
	}

	public boolean parse(PCStat stat, String value) {
		stat.setPenaltyVar(value);
		return true;
	}
}

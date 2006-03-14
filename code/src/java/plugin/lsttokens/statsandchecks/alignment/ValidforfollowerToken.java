package plugin.lsttokens.statsandchecks.alignment;

import pcgen.core.PCAlignment;
import pcgen.persistence.lst.PCAlignmentLstToken;

/**
 * Class deals with VALIDFORFOLLOWER Token
 */
public class ValidforfollowerToken implements PCAlignmentLstToken{

	public String getTokenName() {
		return "VALIDFORFOLLOWER";
	}

	public boolean parse(PCAlignment align, String value) {
		align.setValidForFollower(value.toUpperCase().startsWith("Y"));
		return true;
	}
}

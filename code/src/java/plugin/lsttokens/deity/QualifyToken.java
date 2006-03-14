package plugin.lsttokens.deity;

import pcgen.core.Deity;
import pcgen.persistence.lst.DeityLstToken;

/**
 * Deals with QUALIFY token for deities
 */
public class QualifyToken implements DeityLstToken {

	public String getTokenName() {
		return "QUALIFY";
	}

	public boolean parse(Deity deity, String value) {
		deity.setQualifyString(value);
		return true;
	}
}

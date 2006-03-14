package plugin.lsttokens.domain;

import pcgen.core.Domain;
import pcgen.persistence.lst.DomainLstToken;

/**
 * Deals with the QUALIFY domain LST token
 */
public class QualifyToken implements DomainLstToken {

	public String getTokenName() {
		return "QUALIFY";
	}

	public boolean parse(Domain domain, String value) {
		domain.setQualifyString(value);
		return true;
	}
}

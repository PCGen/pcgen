package plugin.lsttokens.domain;

import pcgen.core.Domain;
import pcgen.persistence.lst.DomainLstToken;

/**
 * Deal with FEAT token 
 */
public class FeatToken implements DomainLstToken {

	public String getTokenName() {
		return "FEAT";
	}

	public boolean parse(Domain domain, String value) {
		domain.addFeat(value);
		return true;
	}
}

package plugin.lsttokens.domain;

import pcgen.core.Domain;
import pcgen.persistence.lst.DomainLstToken;

/**
 * Deals with ABILITY token 
 */
public class AbilityToken implements DomainLstToken {

	public String getTokenName() {
		return "ABILITY";
	}

	public boolean parse(Domain domain, String value) {
		domain.addAbility(value);
		return true;
	}
}

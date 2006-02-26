package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with PROHIBITSPELL Token
 */
public class ProhibitspellToken implements PCClassLstToken {

	public String getTokenName() {
		return "PROHIBITSPELL";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setProhibitSpell(value);
		return true;
	}
}

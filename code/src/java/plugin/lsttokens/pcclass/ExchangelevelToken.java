package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with EXCHANGELEVEL Token
 */
public class ExchangelevelToken implements PCClassLstToken {

	public String getTokenName() {
		return "EXCHANGELEVEL";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setLevelExchange(value);
		return true;
	}
}

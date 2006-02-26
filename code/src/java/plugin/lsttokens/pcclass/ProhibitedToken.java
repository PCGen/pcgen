package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with PROHIBITED Token
 */
public class ProhibitedToken implements PCClassLstToken {

	public String getTokenName() {
		return "PROHIBITED";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setProhibitedString(value);
		return true;
	}
}

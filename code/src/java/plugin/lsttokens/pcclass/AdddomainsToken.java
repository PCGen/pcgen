package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with ADDDOMAINS Token
 */
public class AdddomainsToken implements PCClassLstToken {

	public String getTokenName() {
		return "ADDDOMAINS";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setAddDomains(level, value, ".");
		return true;
	}
}


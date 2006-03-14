package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with KNOWN Token
 */
public class KnownToken implements PCClassLstToken {

	public String getTokenName() {
		return "KNOWN";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.addKnown(level, value);
		return true;
	}
}

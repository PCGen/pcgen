package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with XPPENALTY Token
 */
public class XppenaltyToken implements PCClassLstToken {

	public String getTokenName() {
		return "XPPENALTY";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setXPPenalty(value);
		return true;
	}
}

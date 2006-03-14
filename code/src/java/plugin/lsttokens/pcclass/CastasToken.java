package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with CASTAS Token
 */
public class CastasToken implements PCClassLstToken {

	public String getTokenName() {
		return "CASTAS";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setCastAs(value);
		return true;
	}
}

package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with QUALIFY Token
 */
public class QualifyToken implements PCClassLstToken {

	public String getTokenName() {
		return "QUALIFY";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setQualifyString(value);
		return true;
	}
}

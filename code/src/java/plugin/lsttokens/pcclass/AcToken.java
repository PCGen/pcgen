package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with ADDSPELLLEVEL Token
 */
public class AcToken implements PCClassLstToken {

	public String getTokenName() {
		return "AC";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.getACList().add(value);
		return true;
	}
}

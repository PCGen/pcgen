package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements PCClassLstToken {

	public String getTokenName() {
		return "VISIBLE";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setVisible(value.toUpperCase().startsWith("Y"));
		return true;
	}
}

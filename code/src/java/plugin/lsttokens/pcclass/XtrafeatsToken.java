package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with XTRAFEATS Token
 */
public class XtrafeatsToken implements PCClassLstToken {

	public String getTokenName() {
		return "XTRAFEATS";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		try {
			pcclass.setInitialFeats(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe) {
			return false;
		}
	}
}

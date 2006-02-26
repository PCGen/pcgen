package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with HD Token
 */
public class HdToken implements PCClassLstToken {

	public String getTokenName() {
		return "HD";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		try {
			pcclass.setHitDie(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe) {
			return false;
		}
	}
}

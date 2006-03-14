package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with BAB Token
 */
public class BabToken implements PCClassLstToken {

	public String getTokenName() {
		return "BAB";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setAttackBonusType(value);
		return true;
	}
}

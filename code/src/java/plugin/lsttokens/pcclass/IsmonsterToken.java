package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with ISMONSTER Token
 */
public class IsmonsterToken implements PCClassLstToken {

	public String getTokenName() {
		return "ISMONSTER";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setMonsterFlag(value);
		return true;
	}
}

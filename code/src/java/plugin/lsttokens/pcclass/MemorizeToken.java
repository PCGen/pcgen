package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with MEMORIZE Token
 */
public class MemorizeToken implements PCClassLstToken {

	public String getTokenName() {
		return "MEMORIZE";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setMemorizeSpells(value.startsWith("Y"));
		return true;
	}
}

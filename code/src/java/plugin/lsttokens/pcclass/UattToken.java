package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with UATT Token
 */
public class UattToken implements PCClassLstToken {

	public String getTokenName() {
		return "UATT";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.getUattList().add(value);
		return true;
	}
}

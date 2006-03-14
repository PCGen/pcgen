package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with ABB Token
 */
public class AbbToken implements PCClassLstToken {

	public String getTokenName() {
		return "ABB";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setAbbrev(value);
		return true;
	}
}

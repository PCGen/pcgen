package plugin.lsttokens.subclass;

import pcgen.core.SubClass;
import pcgen.persistence.lst.SubClassLstToken;

/**
 * Class deals with SUBCLASS Token
 */
public class SubclasslevelToken implements SubClassLstToken {

	public String getTokenName() {
		return "SUBCLASS";
	}

	public boolean parse(SubClass subclass, String value) {
		subclass.setName(value);
		return true;
	}
}

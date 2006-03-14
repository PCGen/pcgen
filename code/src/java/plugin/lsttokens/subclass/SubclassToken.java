package plugin.lsttokens.subclass;

import pcgen.core.SubClass;
import pcgen.persistence.lst.SubClassLstToken;

/**
 * Class deals with SUBCLASSLEVEL Token
 */
public class SubclassToken implements SubClassLstToken {

	public String getTokenName() {
		return "SUBCLASSLEVEL";
	}

	public boolean parse(SubClass subclass, String value) {
		subclass.addToLevelArray(value);
		return true;
	}
}

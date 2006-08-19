package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with HASSUBSTITUTIONLEVEL Token
 */
public class HassubstitutionlevelToken implements PCClassLstToken {

	public String getTokenName() {
		return "HASSUBSTITUTIONLEVEL";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setHasSubstitutionClass(value.startsWith("Y"));
		return true;
	}
}

package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with SUBSTITUTIONCLASS Token
 */
public class SubstitutionclassToken implements PCClassLstToken {

	public String getTokenName() {
		return "SUBSTITUTIONCLASS";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setSubstitutionClassString(PCClassLoader.fixParameter(level, value));
		return true;
	}
}

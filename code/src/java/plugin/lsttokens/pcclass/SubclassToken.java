package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with SUBCLASS Token
 */
public class SubclassToken implements PCClassLstToken {

	public String getTokenName() {
		return "SUBCLASS";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setSubClassString(PCClassLoader.fixParameter(level, value));
		return true;
	}
}

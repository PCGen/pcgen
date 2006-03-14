package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with DOMAIN Token
 */
public class DomainToken implements PCClassLstToken {

	public String getTokenName() {
		return "DOMAIN";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.addDomainList(PCClassLoader.fixParameter(level, value));
		return true;
	}
}
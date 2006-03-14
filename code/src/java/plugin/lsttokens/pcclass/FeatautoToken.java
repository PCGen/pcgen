package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with FEATAUTO Token
 */
public class FeatautoToken implements PCClassLstToken {

	public String getTokenName() {
		return "FEATAUTO";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setFeatAutos(level, value);
		return true;
	}
}

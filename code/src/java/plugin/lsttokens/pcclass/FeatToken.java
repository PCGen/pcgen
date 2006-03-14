package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with FEAT Token
 */
public class FeatToken implements PCClassLstToken {

	public String getTokenName() {
		return "FEAT";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.addFeatList(level, value);
		return true;
	}
}

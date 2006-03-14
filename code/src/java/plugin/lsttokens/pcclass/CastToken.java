package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with CAST Token
 */
public class CastToken implements PCClassLstToken {

	public String getTokenName() {
		return "CAST";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		if(level > 0) {
			pcclass.setCastMap(level, value);
			return true;
		}
		Logging.errorPrint("CAST tag without level not allowed!");
		return false;
	}
}

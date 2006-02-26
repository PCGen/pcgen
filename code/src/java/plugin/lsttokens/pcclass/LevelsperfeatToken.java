package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with LEVELSPERFEAT Token
 */
public class LevelsperfeatToken implements PCClassLstToken {

	public String getTokenName() {
		return "LEVELSPERFEAT";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		try {
			pcclass.setLevelsPerFeat(new Integer(value));
			return true;
		}
		catch (NumberFormatException nfe) {
			return false;
		}
	}
}

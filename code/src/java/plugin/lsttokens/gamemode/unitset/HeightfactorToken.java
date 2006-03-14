package plugin.lsttokens.gamemode.unitset;

import pcgen.core.UnitSet;
import pcgen.persistence.lst.UnitSetLstToken;

/**
 * Class deals with HEIGHTFACTOR Token
 */
public class HeightfactorToken implements UnitSetLstToken {

	public String getTokenName() {
		return "HEIGHTFACTOR";
	}

	public boolean parse(UnitSet unitSet, String value) {
		try {
			unitSet.setHeightFactor(Double.parseDouble(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

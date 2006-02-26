package plugin.lsttokens.gamemode.unitset;

import pcgen.core.UnitSet;
import pcgen.persistence.lst.UnitSetLstToken;

/**
 * Class deals with DISTANCEFACTOR Token
 */
public class DistancefactorToken implements UnitSetLstToken {

	public String getTokenName() {
		return "DISTANCEFACTOR";
	}

	public boolean parse(UnitSet unitSet, String value) {
		try {
			unitSet.setDistanceFactor(Double.parseDouble(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

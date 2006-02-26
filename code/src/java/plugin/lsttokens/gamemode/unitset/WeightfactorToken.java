package plugin.lsttokens.gamemode.unitset;

import pcgen.core.UnitSet;
import pcgen.persistence.lst.UnitSetLstToken;

/**
 * Class deals with WEIGHTFACTOR Token
 */
public class WeightfactorToken implements UnitSetLstToken {

	public String getTokenName() {
		return "WEIGHTFACTOR";
	}

	public boolean parse(UnitSet unitSet, String value) {
		try {
			unitSet.setWeightFactor(Double.parseDouble(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

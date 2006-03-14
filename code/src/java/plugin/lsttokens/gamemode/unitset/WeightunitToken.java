package plugin.lsttokens.gamemode.unitset;

import pcgen.core.UnitSet;
import pcgen.persistence.lst.UnitSetLstToken;

/**
 * Class deals with WEIGHTUNIT Token
 */
public class WeightunitToken implements UnitSetLstToken {

	public String getTokenName() {
		return "WEIGHTUNIT";
	}

	public boolean parse(UnitSet unitSet, String value) {
		unitSet.setWeightUnit(value);
		return true;
	}
}

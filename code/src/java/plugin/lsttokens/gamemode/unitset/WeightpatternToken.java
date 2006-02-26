package plugin.lsttokens.gamemode.unitset;

import pcgen.core.UnitSet;
import pcgen.persistence.lst.UnitSetLstToken;

/**
 * Class deals with WEIGHTPATTERN Token
 */
public class WeightpatternToken implements UnitSetLstToken {

	public String getTokenName() {
		return "WEIGHTPATTERN";
	}

	public boolean parse(UnitSet unitSet, String value) {
		unitSet.setWeightDisplayPattern(value);
		return true;
	}
}

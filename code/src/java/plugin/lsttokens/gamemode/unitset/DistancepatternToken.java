package plugin.lsttokens.gamemode.unitset;

import pcgen.core.UnitSet;
import pcgen.persistence.lst.UnitSetLstToken;

/**
 * Class deals with DISTANCEPATTERN Token
 */
public class DistancepatternToken implements UnitSetLstToken {

	public String getTokenName() {
		return "DISTANCEPATTERN";
	}

	public boolean parse(UnitSet unitSet, String value) {
		unitSet.setDistanceDisplayPattern(value);
		return true;
	}
}

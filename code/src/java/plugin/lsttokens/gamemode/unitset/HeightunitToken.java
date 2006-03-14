package plugin.lsttokens.gamemode.unitset;

import pcgen.core.UnitSet;
import pcgen.persistence.lst.UnitSetLstToken;

/**
 * Class deals with HEIGHTUNIT Token
 */
public class HeightunitToken implements UnitSetLstToken {

	public String getTokenName() {
		return "HEIGHTUNIT";
	}

	public boolean parse(UnitSet unitSet, String value) {
		unitSet.setHeightUnit(value);
		return true;
	}
}

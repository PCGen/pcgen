package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with RANGE token 
 */
public class RangeToken implements EquipmentLstToken {

	public String getTokenName() {
		return "RANGE";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setRange(value);
		return true;
	}
}

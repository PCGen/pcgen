package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with SIZE token 
 */
public class SizeToken implements EquipmentLstToken {

	public String getTokenName() {
		return "SIZE";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setSize(value, true);
		return true;
	}
}

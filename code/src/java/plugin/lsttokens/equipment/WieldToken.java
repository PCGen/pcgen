package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with WIELD token 
 */
public class WieldToken implements EquipmentLstToken {

	public String getTokenName() {
		return "WIELD";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setWield(value);
		return true;
	}
}

package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with ACCHECK token 
 */
public class AccheckToken implements EquipmentLstToken {

	public String getTokenName() {
		return "ACCHECK";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setACCheck(value);
		return true;
	}
}

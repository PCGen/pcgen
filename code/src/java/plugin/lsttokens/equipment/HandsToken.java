package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with HANDS token 
 */
public class HandsToken implements EquipmentLstToken {

	public String getTokenName() {
		return "HANDS";
	}

	public boolean parse(Equipment eq, String value) {
		try {
			eq.setHands(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe) {
			return false;
		}
	}
}

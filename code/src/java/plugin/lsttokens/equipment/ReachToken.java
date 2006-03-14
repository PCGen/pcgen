package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with REACH token 
 */
public class ReachToken implements EquipmentLstToken {

	public String getTokenName() {
		return "REACH";
	}

	public boolean parse(Equipment eq, String value) {
		try {
			eq.setReach(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe) {
			return false;
		}
	}
}

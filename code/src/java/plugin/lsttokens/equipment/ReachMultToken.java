package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with REACHMULT token 
 */
public class ReachMultToken implements EquipmentLstToken {

	public String getTokenName() {
		return "REACHMULT";
	}

	public boolean parse(Equipment eq, String value) {
		try {
			eq.setReachMult(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe) {
			return false;
		}
	}
}

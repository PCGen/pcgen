package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with WT token 
 */
public class WtToken implements EquipmentLstToken {

	public String getTokenName() {
		return "WT";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setWeight(value);
		return true;
	}
}

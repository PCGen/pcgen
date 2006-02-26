package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with BASEITEM token 
 */
public class BaseitemToken implements EquipmentLstToken {

	public String getTokenName() {
		return "BASEITEM";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setBaseItem(value);
		return true;
	}
}

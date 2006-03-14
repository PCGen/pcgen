package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with LONGNAME token 
 */
public class LongnameToken implements EquipmentLstToken {

	public String getTokenName() {
		return "LONGNAME";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setLongName(value);
		return true;
	}
}

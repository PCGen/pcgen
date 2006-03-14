package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with QUALIFY token 
 */
public class QualifyToken implements EquipmentLstToken {

	public String getTokenName() {
		return "QUALIFY";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setQualifyString(value);
		return true;
	}
}

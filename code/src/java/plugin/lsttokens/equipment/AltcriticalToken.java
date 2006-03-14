package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with ALTCRITICAL token 
 */
public class AltcriticalToken implements EquipmentLstToken {

	public String getTokenName() {
		return "ALTCRITICAL";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setAltCritMult(value);
		return true;
	}
}

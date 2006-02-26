package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with ALTCRITMULT token 
 */
public class AltcritmultToken implements EquipmentLstToken {

	public String getTokenName() {
		return "ALTCRITMULT";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setAltCritMult(value);
		return true;
	}
}

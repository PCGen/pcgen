package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with CRITMULT token 
 */
public class CritmultToken implements EquipmentLstToken {

	public String getTokenName() {
		return "CRITMULT";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setCritMult(value);
		return true;
	}
}

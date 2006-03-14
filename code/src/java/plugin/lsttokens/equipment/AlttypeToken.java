package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with ALTTYPE token 
 */
public class AlttypeToken implements EquipmentLstToken {

	public String getTokenName() {
		return "ALTTYPE";
	}

	public boolean parse(Equipment eq, String value) {
		eq.addToAltTypeList(value);
		return true;
	}
}

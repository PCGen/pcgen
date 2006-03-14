package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with BONUSTYPE token 
 */
public class BonustypeToken implements EquipmentLstToken {

	public String getTokenName() {
		return "BONUSTYPE";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setBonusType(value);
		return true;
	}
}

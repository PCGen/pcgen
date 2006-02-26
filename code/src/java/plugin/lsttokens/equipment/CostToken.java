package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with COST token 
 */
public class CostToken implements EquipmentLstToken {

	public String getTokenName() {
		return "COST";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setCost(value, true);
		return true;
	}
}

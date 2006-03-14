package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with CHOOSE token 
 */
public class ChooseToken implements EquipmentLstToken {

	public String getTokenName() {
		return "CHOOSE";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setChoiceString(value);
		return true;
	}
}

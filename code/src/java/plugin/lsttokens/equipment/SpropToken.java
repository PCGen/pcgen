package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.core.SpecialProperty;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with SPROP token 
 */
public class SpropToken implements EquipmentLstToken {

	public String getTokenName() {
		return "SPROP";
	}

	public boolean parse(Equipment eq, String value) {
		eq.addSpecialProperty(SpecialProperty.createFromLst(value));
		return true;
	}
}

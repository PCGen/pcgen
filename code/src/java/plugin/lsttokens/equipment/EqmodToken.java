package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with EQMOD token 
 */
public class EqmodToken implements EquipmentLstToken {

	public String getTokenName() {
		return "EQMOD";
	}

	public boolean parse(Equipment eq, String value) {
		eq.addEqModifiers(value, true);
		return true;
	}
}

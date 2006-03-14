package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with DAMAGE token 
 */
public class DamageToken implements EquipmentLstToken {

	public String getTokenName() {
		return "DAMAGE";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setDamage(value);
		return true;
	}
}

package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with RATEOFFIRE token 
 */
public class RateoffireToken implements EquipmentLstToken {

	public String getTokenName() {
		return "RATEOFFIRE";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setRateOfFire(value);
		return true;
	}
}

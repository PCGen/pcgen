package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with FUMBLERANGE token 
 */
public class FumblerangeToken implements EquipmentLstToken {

	public String getTokenName() {
		return "FUMBLERANGE";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setFumbleRange(value);
		return true;
	}
}

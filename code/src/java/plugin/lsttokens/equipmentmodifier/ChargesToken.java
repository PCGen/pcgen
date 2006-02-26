package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with CHARGES token 
 */
public class ChargesToken implements EquipmentModifierLstToken {

	public String getTokenName() {
		return "CHARGES";
	}

	public boolean parse(EquipmentModifier mod, String value) {
		mod.setChargeInfo(value);
		return true;
	}
}

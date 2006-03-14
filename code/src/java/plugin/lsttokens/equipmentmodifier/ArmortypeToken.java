package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with ARMORTYPE token 
 */
public class ArmortypeToken implements EquipmentModifierLstToken {

	public String getTokenName() {
		return "ARMORTYPE";
	}

	public boolean parse(EquipmentModifier mod, String value) {
		mod.setArmorType(value);
		return true;
	}
}

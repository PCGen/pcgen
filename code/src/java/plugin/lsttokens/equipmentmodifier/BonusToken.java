package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with BONUS token 
 */
public class BonusToken implements EquipmentModifierLstToken {

	public String getTokenName() {
		return "BONUS";
	}

	public boolean parse(EquipmentModifier mod, String value) {
		mod.addBonusList(value);
		return true;
	}
}

package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with IGNORES token 
 */
public class IgnoresToken implements EquipmentModifierLstToken {

	public String getTokenName() {
		return "IGNORES";
	}

	public boolean parse(EquipmentModifier mod, String value) {
		mod.setFumbleRange(value);
		return true;
	}
}

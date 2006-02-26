package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with REPLACES token 
 */
public class ReplacesToken implements EquipmentModifierLstToken {

	public String getTokenName() {
		return "REPLACES";
	}

	public boolean parse(EquipmentModifier mod, String value) {
		mod.setReplacement(value);
		return true;
	}
}

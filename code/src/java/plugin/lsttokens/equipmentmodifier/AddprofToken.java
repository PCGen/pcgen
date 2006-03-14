package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with ADDPROF token 
 */
public class AddprofToken implements EquipmentModifierLstToken {

	public String getTokenName() {
		return "ADDPROF";
	}

	public boolean parse(EquipmentModifier mod, String value) {
		mod.setProficiency(value);
		return true;
	}
}

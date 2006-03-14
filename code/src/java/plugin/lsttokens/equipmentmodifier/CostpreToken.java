package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with COSTPRE token 
 */
public class CostpreToken implements EquipmentModifierLstToken {

	public String getTokenName() {
		return "COSTPRE";
	}

	public boolean parse(EquipmentModifier mod, String value) {
		mod.setPreCost(value);
		return true;
	}
}

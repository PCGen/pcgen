package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with COSTDOUBLE token 
 */
public class CostdoubleToken implements EquipmentModifierLstToken {

	public String getTokenName() {
		return "COSTDOUBLE";
	}

	public boolean parse(EquipmentModifier mod, String value) {
		mod.setCostDouble(value.toUpperCase().startsWith("Y"));
		return true;
	}
}

package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with ITYPE token 
 */
public class ItypeToken implements EquipmentModifierLstToken {

	public String getTokenName() {
		return "ITYPE";
	}

	public boolean parse(EquipmentModifier mod, String value) {
		mod.setItemType(value);
		return true;
	}
}

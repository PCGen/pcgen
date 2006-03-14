package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with NAMEOPT token 
 */
public class NameoptToken implements EquipmentModifierLstToken {

	public String getTokenName() {
		return "NAMEOPT";
	}

	public boolean parse(EquipmentModifier mod, String value) {
		mod.setNamingOption(value);
		return true;
	}
}

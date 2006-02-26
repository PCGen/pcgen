package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with FUMBLERANGE token 
 */
public class FumblerangeToken implements EquipmentModifierLstToken {

	public String getTokenName() {
		return "FUMBLERANGE";
	}

	public boolean parse(EquipmentModifier mod, String value) {
		mod.setFumbleRange(value);
		return true;
	}
}

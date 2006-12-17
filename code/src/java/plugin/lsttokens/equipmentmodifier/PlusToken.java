package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with PLUS token 
 */
public class PlusToken implements EquipmentModifierLstToken
{

	public String getTokenName()
	{
		return "PLUS";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		mod.setPlus(value);
		return true;
	}
}

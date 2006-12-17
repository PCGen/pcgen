package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.core.SpecialProperty;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with SPROP token 
 */
public class SpropToken implements EquipmentModifierLstToken
{

	public String getTokenName()
	{
		return "SPROP";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		mod.addSpecialProperty(SpecialProperty.createFromLst(value));
		return true;
	}
}

package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with COST token 
 */
public class CostToken implements EquipmentModifierLstToken
{

	public String getTokenName()
	{
		return "COST";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		mod.setCost(value);
		return true;
	}
}

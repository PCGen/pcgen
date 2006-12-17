package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with ASSIGNTOALL token 
 */
public class AssigntoallToken implements EquipmentModifierLstToken
{

	public String getTokenName()
	{
		return "ASSIGNTOALL";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		mod.setAssignment(value);
		return true;
	}
}

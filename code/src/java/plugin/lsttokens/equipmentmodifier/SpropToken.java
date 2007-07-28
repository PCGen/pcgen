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
		if (".CLEAR".equals(value))
		{
			mod.clearSpecialProperties();
		}
		else
		{
			mod.addSpecialProperty(SpecialProperty.createFromLst(value));
		}
		return true;
	}
}

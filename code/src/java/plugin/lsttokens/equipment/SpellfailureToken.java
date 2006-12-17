package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with SPELLFAILURE token 
 */
public class SpellfailureToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "SPELLFAILURE";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.setSpellFailure(value);
		return true;
	}
}

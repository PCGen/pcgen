package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with EDR token 
 */
public class EdrToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "EDR";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.seteDR(value);
		return true;
	}
}

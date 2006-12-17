package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with CRITRANGE token 
 */
public class CritrangeToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "CRITRANGE";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.setCritRange(value);
		return true;
	}
}

package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with BASEQTY token 
 */
public class BaseqtyToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "BASEQTY";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.setBaseQty(value);
		return true;
	}
}

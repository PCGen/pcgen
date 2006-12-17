package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with MAXDEX token 
 */
public class MaxdexToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "MAXDEX";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.setMaxDex(value);
		return true;
	}
}

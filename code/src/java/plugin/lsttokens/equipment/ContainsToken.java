package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with CONTAINS token 
 */
public class ContainsToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "CONTAINS";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.setContainer(value);
		return true;
	}
}

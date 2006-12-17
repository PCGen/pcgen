package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with SLOTS token 
 */
public class SlotsToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "SLOTS";
	}

	public boolean parse(Equipment eq, String value)
	{
		try
		{
			eq.setSlots(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}
}

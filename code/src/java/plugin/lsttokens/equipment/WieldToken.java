package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with WIELD token 
 */
public class WieldToken implements EquipmentLstToken
{

	/**
	 * Get token name
	 * @return token name 
	 */
	public String getTokenName()
	{
		return "WIELD";
	}

	/**
	 * Parse WIELD token
	 * @param eq 
	 * @param value 
	 * @return true
	 */
	public boolean parse(Equipment eq, String value)
	{
		eq.setWield(value);
		return true;
	}
}

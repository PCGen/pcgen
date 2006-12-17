package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with ALTCRITRANGE token 
 */
public class AltcritrangeToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "ALTCRITRANGE";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.setAltCritRange(value);
		return true;
	}
}

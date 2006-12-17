package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with ALTDAMAGE token 
 */
public class AltdamageToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "ALTDAMAGE";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.setAltDamage(value);
		return true;
	}
}

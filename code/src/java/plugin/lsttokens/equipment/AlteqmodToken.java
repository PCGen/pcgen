package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with ALTEQMOD token 
 */
public class AlteqmodToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "ALTEQMOD";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.addEqModifiers(value, false);
		return true;
	}
}

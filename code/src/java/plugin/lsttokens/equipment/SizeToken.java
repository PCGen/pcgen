package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with SIZE token 
 */
public class SizeToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "SIZE";
	}

	public boolean parse(Equipment eq, String value)
	{
		if (value.length() > 1) {
			value = value.toUpperCase().substring(0, 1);
		}
		
		SizeAdjustment sa = SettingsHandler.getGame().getSizeAdjustmentNamed(value);
		eq.setSize(sa);
		eq.setBaseSize(sa);
		return true;
	}
}

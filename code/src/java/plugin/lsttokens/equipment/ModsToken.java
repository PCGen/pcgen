package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with MODS token 
 */
public class ModsToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "MODS";
	}

	public boolean parse(Equipment eq, String value)
	{
		switch (value.charAt(0))
		{
			case 'R':
			case 'r':
				eq.setModifiersAllowed(true);
				eq.setModifiersRequired(true);
				break;

			case 'Y':
			case 'y':
				eq.setModifiersAllowed(true);
				eq.setModifiersRequired(false);
				break;

			case 'N':
			case 'n':
				eq.setModifiersAllowed(false);
				eq.setModifiersRequired(false);
				break;

			default:
				return false;
		}
		return true;
	}
}

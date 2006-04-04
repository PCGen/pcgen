package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with ALTCRITMULT token
 */
public class AltcritmultToken implements EquipmentLstToken {

	public String getTokenName() {
		return "ALTCRITMULT";
	}

	public boolean parse(Equipment eq, String value)
	{
		if ((value.length() > 0) && (value.charAt(0) == 'x'))
		{
			try
			{
				eq.setAltCritMult(Integer.parseInt(value.substring(1)));
			}
			catch (NumberFormatException nfe)
			{
				return false;
			}
			return true;
		}
		else if (value.equals("-"))
		{
			eq.setAltCritMult(-1);
			return true;
		}
		return false;
	}
}

package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with CRITMULT token
 */
public class CritmultToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "CRITMULT";
	}

	public boolean parse(Equipment eq, String value)
	{
		if ((value.length() > 0)
			&& (Character.toLowerCase(value.charAt(0)) == 'x'))
		{
			try
			{
				eq.setCritMult(Integer.parseInt(value.substring(1)));
			}
			catch (NumberFormatException nfe)
			{
				return false;
			}
			return true;
		}
		else if (value.equals("-"))
		{
			eq.setCritMult(-1);
			return true;
		}
		return false;
	}
}

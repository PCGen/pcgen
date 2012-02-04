package plugin.lsttokens.eqslot;

import java.util.StringTokenizer;

import pcgen.core.character.EquipSlot;
import pcgen.persistence.lst.EquipSlotLstToken;
import pcgen.util.Logging;

/**
 * Class deals with CONTAINS Token
 */
public class ContainsToken implements EquipSlotLstToken
{

	public String getTokenName()
	{
		return "CONTAINS";
	}

	public boolean parse(EquipSlot eqSlot, String value, String gameMode)
	{
		if (value == null || value.length()==0)
		{
			Logging.log(Logging.LST_ERROR, "Invalid empty " + getTokenName() + " value."); 
			return false;
		}
		
		final StringTokenizer token = new StringTokenizer(value, "=");

		if (token.countTokens() < 2)
		{
			Logging.log(Logging.LST_ERROR, "Missing = in value '" + value
				+ "' of " + getTokenName() + ":" + value);
			return false;
		}
		else if (token.countTokens() > 2)
		{
			Logging.log(Logging.LST_ERROR, "Too many = in value '" + value
				+ "' of " + getTokenName() + ":" + value);
			return false;
		}
		
		final String type = token.nextToken();
		final String numString = token.nextToken();
		final int num;

		if (numString.equals("*"))
		{
			num = 9999;
		}
		else
		{
			num = Integer.parseInt(numString);
		}

		
		final String[] types=type.split(",");
		for (String pair : types)
		{
			eqSlot.addContainedType(pair);
		}
		eqSlot.setContainNum(num);
		return true;
	}
}

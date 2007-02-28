package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.util.Logging;

/**
 * Class deals with XPCOST Token
 */
public class XpcostToken implements SpellLstToken
{

	/**
	 * Get the token name
	 * @return token name 
	 */
	public String getTokenName()
	{
		return "XPCOST";
	}

	/**
	 * Parse XPCOST token
	 * 
	 * @param spell 
	 * @param value 
	 * @return true
	 */
	public boolean parse(Spell spell, String value)
	{
		try
		{
			int xpCost = Integer.parseInt(value);
			if (xpCost < 0)
			{
				Logging.errorPrint(getTokenName()
					+ " can not have a negative value");
				return false;
			}
			spell.setXPCost(xpCost);
		}
		catch (NumberFormatException ignore)
		{
			Logging.errorPrint(getTokenName()
				+ " must be an integer (greater than or equal to zero)");
			return false;
		}
		return true;
	}
}

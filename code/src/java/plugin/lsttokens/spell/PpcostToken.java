package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.util.Logging;

/**
 * Class deals with PPCOST Token
 */
public class PpcostToken implements SpellLstToken
{

	public String getTokenName()
	{
		return "PPCOST";
	}

	public boolean parse(Spell spell, String value)
	{
		try
		{
			int ppCost = Integer.parseInt(value);
			if (ppCost < 0)
			{
				Logging.errorPrint(getTokenName()
					+ " can not have a negative value");
				return false;
			}
			spell.setPPCost(ppCost);
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

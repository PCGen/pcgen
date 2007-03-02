package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.util.Logging;

/**
 * Class deals with CT Token
 */
public class CtToken implements SpellLstToken
{

	public String getTokenName()
	{
		return "CT";
	}

	public boolean parse(Spell spell, String value)
	{
		try
		{
			int ct = Integer.parseInt(value);
			if (ct < 0)
			{
				Logging.errorPrint(getTokenName()
					+ " can not have a negative value");
				return false;
			}
			spell.setCastingThreshold(ct);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
				+ " must be an integer (greater than or equal to zero)");
			return false;
		}
	}
}

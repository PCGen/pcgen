package plugin.lsttokens.ability;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Ability;
import pcgen.persistence.lst.AbilityLstToken;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 * Class deals with ADDSPELLLEVEL Token
 */
public class AddspelllevelToken implements AbilityLstToken
{

	public String getTokenName()
	{
		return "ADDSPELLLEVEL";
	}

	public boolean parse(Ability ability, String value)
	{
		try
		{
			ability.put(IntegerKey.ADD_SPELL_LEVEL, Delta.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Bad addSpellLevel " + value);
		}
		return false;
	}
}

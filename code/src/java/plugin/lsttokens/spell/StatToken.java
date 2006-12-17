package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with STAT Token
 */
public class StatToken implements SpellLstToken
{

	public String getTokenName()
	{
		return "STAT";
	}

	public boolean parse(Spell spell, String value)
	{
		spell.setStat(value);
		return true;
	}
}

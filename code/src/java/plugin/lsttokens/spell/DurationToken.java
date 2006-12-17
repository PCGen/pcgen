package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with DURATION Token
 */
public class DurationToken implements SpellLstToken
{

	public String getTokenName()
	{
		return "DURATION";
	}

	public boolean parse(Spell spell, String value)
	{
		spell.setDuration(value);
		return true;
	}
}

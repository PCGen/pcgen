package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with CASTTIME Token
 */
public class CasttimeToken implements SpellLstToken
{

	public String getTokenName()
	{
		return "CASTTIME";
	}

	public boolean parse(Spell spell, String value)
	{
		spell.setCastingTime(value);
		return true;
	}
}

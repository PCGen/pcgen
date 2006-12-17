package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with SPELLRES Token
 */
public class SpellresToken implements SpellLstToken
{

	public String getTokenName()
	{
		return "SPELLRES";
	}

	public boolean parse(Spell spell, String value)
	{
		spell.setSpellResistance(value);
		return true;
	}
}

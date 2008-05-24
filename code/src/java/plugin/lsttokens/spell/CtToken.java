package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

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
		return false;
	}
}

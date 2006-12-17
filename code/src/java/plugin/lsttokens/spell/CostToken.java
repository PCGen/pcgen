package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with COST Token
 */
public class CostToken implements SpellLstToken
{

	public String getTokenName()
	{
		return "COST";
	}

	public boolean parse(Spell spell, String value)
	{
		spell.setCost(value);
		return true;
	}
}

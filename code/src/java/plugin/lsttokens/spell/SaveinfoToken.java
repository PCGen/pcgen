package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with SAVEINFO Token
 */
public class SaveinfoToken implements SpellLstToken
{

	public String getTokenName()
	{
		return "SAVEINFO";
	}

	public boolean parse(Spell spell, String value)
	{
		spell.setSaveInfo(value);
		return true;
	}
}

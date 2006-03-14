package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with COMPS Token
 */
public class CompsToken implements SpellLstToken {

	public String getTokenName() {
		return "COMPS";
	}

	public boolean parse(Spell spell, String value) {
		spell.setComponentList(value);
		return true;
	}
}


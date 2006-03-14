package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with RANGE Token
 */
public class RangeToken implements SpellLstToken {

	public String getTokenName() {
		return "RANGE";
	}

	public boolean parse(Spell spell, String value) {
		spell.setRange(value);
		return true;
	}
}

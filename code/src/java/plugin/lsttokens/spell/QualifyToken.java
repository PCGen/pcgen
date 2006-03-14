package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with QUALIFY Token
 */
public class QualifyToken implements SpellLstToken {

	public String getTokenName() {
		return "QUALIFY";
	}

	public boolean parse(Spell spell, String value) {
		spell.setQualifyString(value);
		return true;
	}
}

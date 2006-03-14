package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with XPCOST Token
 */
public class XpcostToken implements SpellLstToken {

	public String getTokenName() {
		return "XPCOST";
	}

	public boolean parse(Spell spell, String value) {
		spell.setXPCost(value);
		return true;
	}
}

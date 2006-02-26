package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with PPCOST Token
 */
public class PpcostToken implements SpellLstToken {

	public String getTokenName() {
		return "PPCOST";
	}

	public boolean parse(Spell spell, String value) {
		try {
			spell.setPPCost(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

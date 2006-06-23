package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.lst.SpellLstToken;

/**
 * Class deals with XPCOST Token
 */
public class XpcostToken implements SpellLstToken {

	/**
     * Get the token name
     * @return token name 
	 */
    public String getTokenName() {
		return "XPCOST";
	}

	/**
     * Parse XPCOST token
     * 
	 * @param spell 
	 * @param value 
	 * @return true
	 */
    public boolean parse(Spell spell, String value) {
		spell.setXPCost(value);
		return true;
	}
}

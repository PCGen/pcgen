package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with SPELLLIST Token
 */
public class SpelllistToken implements PCClassLstToken {

	public String getTokenName() {
		return "SPELLLIST";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setSpellLevelString(value);
		return true;
	}
}

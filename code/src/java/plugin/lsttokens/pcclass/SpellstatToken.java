package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with SPELLSTAT Token
 */
public class SpellstatToken implements PCClassLstToken {

	public String getTokenName() {
		return "SPELLSTAT";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setSpellBaseStat(value);
		return true;
	}
}

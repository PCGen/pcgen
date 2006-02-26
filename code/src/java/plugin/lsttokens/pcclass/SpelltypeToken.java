package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with SPELLTYPE Token
 */
public class SpelltypeToken implements PCClassLstToken {

	public String getTokenName() {
		return "SPELLTYPE";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setSpellType(value);
		return true;
	}
}

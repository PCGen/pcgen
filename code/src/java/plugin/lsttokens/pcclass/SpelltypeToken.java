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
		/*
		 * CONSIDER In the future it may be useful here to check for "" or
		 * "None" and filter those out (never set the spell type) - thpr 11/9/06
		 */
		pcclass.setSpellType(value);
		return true;
	}
}

package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with KNOWNSPELLS Token
 */
public class KnownspellsToken implements PCClassLstToken {

	public String getTokenName() {
		return "KNOWNSPELLS";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.addKnownSpellsList(value);
		return true;
	}
}

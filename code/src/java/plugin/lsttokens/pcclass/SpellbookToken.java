package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with SPELLBOOK Token
 */
public class SpellbookToken implements PCClassLstToken {

	public String getTokenName() {
		return "SPELLBOOK";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setSpellBookUsed(value.startsWith("Y"));
		return true;
	}
}

package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with HASSPELLFORMULA Token
 */
public class HasspellformulaToken implements PCClassLstToken {

	public String getTokenName() {
		return "HASSPELLFORMULA";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setHasSpellFormula(true);
		return true;
	}
}

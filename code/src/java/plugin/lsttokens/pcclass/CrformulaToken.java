package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with CRFORMULA Token
 */
public class CrformulaToken implements PCClassLstToken {

	public String getTokenName() {
		return "CRFORMULA";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setCRFormula(value);
		return true;
	}
}

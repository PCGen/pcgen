package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with SPECIALTYKNOWN Token
 */
public class SpecialtyknownToken implements PCClassLstToken {

	public String getTokenName() {
		return "SPECIALTYKNOWN";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.getSpecialtyKnownList().add(value);
		return true;
	}
}

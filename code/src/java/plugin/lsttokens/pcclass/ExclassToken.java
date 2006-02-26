package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with EXCLASS Token
 */
public class ExclassToken implements PCClassLstToken {

	public String getTokenName() {
		return "EXCLASS";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setExClass(value);
		return true;
	}
}

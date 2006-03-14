package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with DEITY Token
 */
public class DeityToken implements PCClassLstToken {

	public String getTokenName() {
		return "DEITY";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		String[] deities = value.split("\\|");
		pcclass.setDeityList( CoreUtility.arrayToList(deities) );
		return true;
	}
}

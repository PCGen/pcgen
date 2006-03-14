package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with KNOWNSPELLSFROMSPECIALTY Token
 */
public class KnownspellsfromspecialtyToken implements PCClassLstToken {

	public String getTokenName() {
		return "KNOWNSPELLSFROMSPECIALTY";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		try {
			pcclass.setNumSpellsFromSpecialty(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe) {
			return false;
		}
	}
}

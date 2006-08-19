package plugin.lsttokens.substitutionlevel;

import pcgen.core.SubstitutionClass;
import pcgen.persistence.lst.SubstitutionClassLstToken;

/**
 * Class deals with SUBSTITUTIONLEVEL Token
 */
public class SubstitutionlevelToken implements SubstitutionClassLstToken {

	public String getTokenName() {
		return "SUBSTITUTIONLEVEL";
	}

	public boolean parse(SubstitutionClass substitutionclass, String value) {
		substitutionclass.addToLevelArray(value);
		return true;
	}
}

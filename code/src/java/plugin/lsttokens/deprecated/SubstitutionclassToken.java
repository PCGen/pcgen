package plugin.lsttokens.deprecated;

import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SUBSTITUTIONCLASS Token
 */
public class SubstitutionclassToken implements PCClassLstToken, DeprecatedToken {

	public String getTokenName() {
		return "SUBSTITUTIONCLASS";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		Logging.errorPrint("You have used a Tag (SUBSTITUTIONCLASS) which was never processed in PCGen - it has been removed");
		return false;
	}

	public String getMessage(PObject obj, String value) {
		return "You have used a Tag (SUBSTITUTIONCLASS) NOT at the start of a line..." +
				"this was never processed in PCGen - it has been removed";
	}
}

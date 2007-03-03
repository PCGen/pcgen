package plugin.lsttokens.deprecated;

import pcgen.core.PObject;
import pcgen.persistence.lst.AddLstToken;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.util.Logging;

public class AddInitToken implements DeprecatedToken, AddLstToken {

	public boolean parse(PObject target, String value, int level) {
		Logging.errorPrint("ADD:INIT is deprecated");
		return true;
	}

	public String getTokenName() {
		return "INIT";
	}

	public String getMessage(PObject obj, String value) {
		return "  Note that ADD:INIT does not function - "
				+ "you are not getting what you expect!";
	}

}

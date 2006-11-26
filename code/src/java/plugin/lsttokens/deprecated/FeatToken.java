package plugin.lsttokens.deprecated;

import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with FEAT Token
 */
public class FeatToken implements PCClassLstToken, DeprecatedToken {

	public String getTokenName() {
		return "FEAT";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.addFeatList(level, value);
		return true;
	}

	public String getMessage(PObject obj, String value) {
		return "Use ADD:FEAT, VFEAT or FEATAUTO instead.";
	}

}
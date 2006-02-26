package plugin.lsttokens.deity;

import pcgen.core.Deity;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.lst.DeityLstToken;

import java.util.List;

/**
 * Class deals with PANTHEON Token
 */
public class PantheonToken implements DeityLstToken{

	public String getTokenName() {
		return "PANTHEON";
	}

	public boolean parse(Deity deity, String value) {
		if(value.length() > 0) {
			String[] pantheons = value.split("\\|");
			List pantheonList = CoreUtility.arrayToList(pantheons);
			deity.setPantheonList(pantheonList);
			return true;
		}
		return false;
	}
}

package plugin.lsttokens.deity;

import pcgen.core.Deity;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.lst.DeityLstToken;

import java.util.List;

/**
 * Class deals with RACE Token
 */
public class RaceToken implements DeityLstToken{

	public String getTokenName() {
		return "RACE";
	}

	public boolean parse(Deity deity, String value) {
		if(value.length() > 0) {
			String[] races = value.split("\\|");
			List<String> raceList = CoreUtility.arrayToList(races);
			deity.setRacePantheonList(raceList);
			return true;
		}
		return false;
	}
}

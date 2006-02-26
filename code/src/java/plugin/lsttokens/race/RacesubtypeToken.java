package plugin.lsttokens.race;

import java.util.StringTokenizer;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with RACESUBTYPE Token
 */
public class RacesubtypeToken implements RaceLstToken {

	public String getTokenName() {
		return "RACESUBTYPE";
	}

	public boolean parse(Race race, String value) {
		StringTokenizer tok = new StringTokenizer(value, "|");
		while (tok.hasMoreTokens()) {
			String subType = tok.nextToken();
			race.addRacialSubType(subType);
		}
		return true;
	}
}

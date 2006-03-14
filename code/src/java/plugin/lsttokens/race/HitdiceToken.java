package plugin.lsttokens.race;

import java.util.StringTokenizer;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with HITDICE Token
 */
public class HitdiceToken implements RaceLstToken {

	public String getTokenName() {
		return "HITDICE";
	}

	public boolean parse(Race race, String value) {
		try {
			final StringTokenizer hitdice = new StringTokenizer(value, ",");

			if (hitdice.countTokens() != 2) {
				return false;
			}
			race.setHitDice(Integer.parseInt(hitdice.nextToken()));
			race.setHitDiceSize(Integer.parseInt(hitdice.nextToken()));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

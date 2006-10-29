package plugin.lsttokens.race;

import java.util.StringTokenizer;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with HITDICEADVANCEMENT Token
 */
public class HitdiceadvancementToken implements RaceLstToken {

	public String getTokenName() {
		return "HITDICEADVANCEMENT";
	}

	public boolean parse(Race race, String value) {
		try {
			final StringTokenizer advancement = new StringTokenizer(value, ",");

			final int[] hitDiceAdvancement = new int[advancement.countTokens()];

			for (int x = 0; x < hitDiceAdvancement.length; ++x) {
				String temp = advancement.nextToken();

				if ((temp.length() > 0) && (temp.charAt(0) == '*')) {
					race.setAdvancementUnlimited(true);
				}

				if (race.isAdvancementUnlimited()) {
					hitDiceAdvancement[x] = -1;
				}
				else {
					hitDiceAdvancement[x] = Integer.parseInt(temp);
				}
			}

			race.setHitDiceAdvancement(hitDiceAdvancement);
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

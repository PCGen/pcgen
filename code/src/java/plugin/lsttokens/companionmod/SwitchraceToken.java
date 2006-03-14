package plugin.lsttokens.companionmod;

import pcgen.core.character.CompanionMod;
import pcgen.persistence.lst.CompanionModLstToken;

import java.util.StringTokenizer;

/**
 * Class deals with SWITCHRACE: Token
 */
public class SwitchraceToken implements CompanionModLstToken {

	public String getTokenName() {
		return "SWITCHRACE";
	}

	public boolean parse(CompanionMod cmpMod, String value) {
		try {
			final StringTokenizer aTok = new StringTokenizer(value, "|", false);
			final String currT = aTok.nextToken();
			final String toT = aTok.nextToken();
			cmpMod.getSwitchRaceMap().put(currT.toUpperCase(), toT.toUpperCase());
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
}

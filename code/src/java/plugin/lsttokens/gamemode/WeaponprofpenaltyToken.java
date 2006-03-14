package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with WEAPONNONPROFPENALTY Token
 */
public class WeaponprofpenaltyToken implements GameModeLstToken {

	public String getTokenName() {
		return "WEAPONNONPROFPENALTY";
	}

	public boolean parse(GameMode gameMode, String value) {
		try {
			gameMode.setNonProfPenalty(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

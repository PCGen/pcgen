package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SQUARESIZE Token
 */
public class SquaresizeToken implements GameModeLstToken {

	public String getTokenName() {
		return "SQUARESIZE";
	}

	public boolean parse(GameMode gameMode, String value) {
		try {
			gameMode.setSquareSize(Double.parseDouble(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

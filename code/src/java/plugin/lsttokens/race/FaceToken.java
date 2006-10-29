package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with FACE Token
 */
public class FaceToken implements RaceLstToken {

	public String getTokenName() {
		return "FACE";
	}

	public boolean parse(Race race, String value) {
		int commaLoc = value.indexOf(",");
		if(commaLoc > -1) {
			double width;
			double height;
			try {
				width = Double.parseDouble(value.substring(0, commaLoc - 1).trim());
			}
			catch (NumberFormatException nfe) {
				width = 5;
			}
			
			try {
				height = Double.parseDouble(value.substring(commaLoc + 1).trim());
			}
			catch (NumberFormatException ne) {
				height = 5;
			}
			race.setFace(width, height);
		}
		else {
			double width;
			try {
				width = Double.parseDouble(value);
			}
			catch (NumberFormatException nfe) {
				width = 5;
			}
			race.setFace(width, 0);
		}
		return true;
	}
}



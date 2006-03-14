package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.persistence.lst.WieldCategoryLoader;

/**
 * Class deals with WIELDCATEGORY Token
 */
public class WieldcategoryToken implements GameModeLstToken {

	public String getTokenName() {
		return "WIELDCATEGORY";
	}

	public boolean parse(GameMode gameMode, String value) {
		try {
			WieldCategoryLoader catDiceLoader = new WieldCategoryLoader();
			catDiceLoader.parseLine(gameMode, "WIELDCATEGORY:" + value);
			return true;
		}
		catch(Exception e) {
			return false;
		}

	}
}

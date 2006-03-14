package plugin.lsttokens.gamemode.wieldcategory;

import pcgen.core.character.WieldCategory;
import pcgen.persistence.lst.WieldCategoryLstToken;

/**
 * Class deals with HANDS Token
 */
public class HandsToken implements WieldCategoryLstToken {

	public String getTokenName() {
		return "HANDS";
	}

	public boolean parse(WieldCategory cat, String value) {
		// Minimum hands required to wield weapon
		try {
			cat.setHands(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

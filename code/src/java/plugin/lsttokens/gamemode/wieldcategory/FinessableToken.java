package plugin.lsttokens.gamemode.wieldcategory;

import pcgen.core.character.WieldCategory;
import pcgen.persistence.lst.WieldCategoryLstToken;

/**
 * Class deals with FINESSABLE Token
 */
public class FinessableToken implements WieldCategoryLstToken {

	public String getTokenName() {
		return "FINESSABLE";
	}

	public boolean parse(WieldCategory cat, String value) {
		// Is this category finessable?
		if (value.toUpperCase().startsWith("Y")) {
			cat.setFinessable(true);
		}
		return true;
	}
}

package plugin.lsttokens.gamemode.wieldcategory;

import pcgen.core.character.WieldCategory;
import pcgen.persistence.lst.WieldCategoryLstToken;

/**
 * Class deals with ZERO Token
 */
public class ZeroToken implements WieldCategoryLstToken {

	/**
     * Get token name
     * @return Token name
	 */
    public String getTokenName() {
		return "ZERO";
	}

    /**
     * Parse ZERO token
     * @param cat 
     * @param value 
     * @return true
     */
	public boolean parse(WieldCategory cat, String value) {
		//The wield category steps
		cat.setWCStep(0, value);
		return true;
	}
}

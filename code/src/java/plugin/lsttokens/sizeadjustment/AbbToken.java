package plugin.lsttokens.sizeadjustment;

import pcgen.core.SizeAdjustment;
import pcgen.persistence.lst.SizeAdjustmentLstToken;

/**
 * Class deals with ABB Token for size adjustment
 */
public class AbbToken implements SizeAdjustmentLstToken {

    /**
     * Get the token name
     * @return token name
     */
	public String getTokenName() {
		return "ABB";
	}

    /**
     * Parse the abbreviation token
     * @param sa 
     * @param value true
     * @return true
     */
	public boolean parse(SizeAdjustment sa, String value) {
		sa.setAbbreviation(value);
		return true;
	}
}

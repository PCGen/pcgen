package plugin.lsttokens.sizeadjustment;

import pcgen.core.SizeAdjustment;
import pcgen.persistence.lst.SizeAdjustmentLstToken;

/**
 * Class deals with ABB Token
 */
public class AbbToken implements SizeAdjustmentLstToken {

	public String getTokenName() {
		return "ABB";
	}

	public boolean parse(SizeAdjustment sa, String value) {
		sa.setAbbreviation(value);
		return true;
	}
}

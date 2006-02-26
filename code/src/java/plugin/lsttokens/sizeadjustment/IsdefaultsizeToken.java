package plugin.lsttokens.sizeadjustment;

import pcgen.core.SizeAdjustment;
import pcgen.persistence.lst.SizeAdjustmentLstToken;

/**
 * Class deals with ISDEFAULTSIZE Token
 */
public class IsdefaultsizeToken implements SizeAdjustmentLstToken {

	public String getTokenName() {
		return "ISDEFAULTSIZE";
	}

	public boolean parse(SizeAdjustment sa, String value) {
		sa.setIsDefaultSize(value.toUpperCase().startsWith("Y"));
		return true;
	}
}

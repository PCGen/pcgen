package plugin.lsttokens.subclass;

import pcgen.core.SubClass;
import pcgen.persistence.lst.SubClassLstToken;

/**
 * Class deals with PROHIBITCOST Token
 */
public class ProhibitcostToken implements SubClassLstToken {

	public String getTokenName() {
		return "PROHIBITCOST";
	}

	public boolean parse(SubClass subclass, String value) {
		try {
			subclass.setProhibitCost(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}

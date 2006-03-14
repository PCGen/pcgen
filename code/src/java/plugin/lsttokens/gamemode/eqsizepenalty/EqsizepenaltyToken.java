package plugin.lsttokens.gamemode.eqsizepenalty;

import pcgen.core.PObject;
import pcgen.persistence.lst.EqSizePenaltyLstToken;

/**
 * Class deals with EQSIZEPENALTY Token
 */
public class EqsizepenaltyToken implements EqSizePenaltyLstToken {

	public String getTokenName() {
		return "EQSIZEPENALTY";
	}

	public boolean parse(PObject penalty, String value) {
		penalty.setName(value);
		return true;
	}
}

package plugin.lsttokens.rules;

import pcgen.core.RuleCheck;
import pcgen.persistence.lst.RuleCheckLstToken;

/**
 * Class deals with PARM Token
 */
public class ParmToken implements RuleCheckLstToken {

	public String getTokenName() {
		return "PARM";
	}

	public boolean parse(RuleCheck rule, String value) {
		rule.setParameter(value);
		return true;
	}
}

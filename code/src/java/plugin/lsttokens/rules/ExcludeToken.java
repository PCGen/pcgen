package plugin.lsttokens.rules;

import pcgen.core.RuleCheck;
import pcgen.persistence.lst.RuleCheckLstToken;

/**
 * Class deals with EXCLUDE Token
 */
public class ExcludeToken implements RuleCheckLstToken {

	public String getTokenName() {
		return "EXCLUDE";
	}

	public boolean parse(RuleCheck rule, String value) {
		rule.setExclude(value);
		return true;
	}
}

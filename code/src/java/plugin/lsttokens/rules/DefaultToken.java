package plugin.lsttokens.rules;

import pcgen.core.RuleCheck;
import pcgen.persistence.lst.RuleCheckLstToken;

/**
 * Class deals with DEFAULT Token
 */
public class DefaultToken implements RuleCheckLstToken {

	public String getTokenName() {
		return "DEFAULT";
	}

	public boolean parse(RuleCheck rule, String value) {
		rule.setDefault(value);
		return true;
	}
}

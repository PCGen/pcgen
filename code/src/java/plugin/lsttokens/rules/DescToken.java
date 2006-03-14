package plugin.lsttokens.rules;

import pcgen.core.RuleCheck;
import pcgen.persistence.lst.RuleCheckLstToken;

/**
 * Class deals with DESC Token
 */
public class DescToken implements RuleCheckLstToken {

	public String getTokenName() {
		return "DESC";
	}

	public boolean parse(RuleCheck rule, String value) {
		rule.setDesc(value);
		return true;
	}
}

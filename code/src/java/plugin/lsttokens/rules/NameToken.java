package plugin.lsttokens.rules;

import pcgen.core.RuleCheck;
import pcgen.persistence.lst.RuleCheckLstToken;

/**
 * Class deals with NAME Token
 */
public class NameToken implements RuleCheckLstToken {

	public String getTokenName() {
		return "NAME";
	}

	public boolean parse(RuleCheck rule, String value) {
		rule.setName(value);
		return true;
	}
}

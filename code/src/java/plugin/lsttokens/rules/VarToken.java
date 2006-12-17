package plugin.lsttokens.rules;

import pcgen.core.RuleCheck;
import pcgen.persistence.lst.RuleCheckLstToken;

/**
 * Class deals with VAR Token
 */
public class VarToken implements RuleCheckLstToken
{

	public String getTokenName()
	{
		return "VAR";
	}

	public boolean parse(RuleCheck rule, String value)
	{
		rule.setVariable(value);
		return true;
	}
}

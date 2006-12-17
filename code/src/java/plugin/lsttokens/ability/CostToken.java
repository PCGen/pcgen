package plugin.lsttokens.ability;

import pcgen.core.Ability;
import pcgen.persistence.lst.AbilityLstToken;

/**
 * Deal with COST Token
 */
public class CostToken implements AbilityLstToken
{

	public String getTokenName()
	{
		return "COST";
	}

	public boolean parse(Ability ability, String value)
	{
		ability.setCost(value);
		return true;
	}
}

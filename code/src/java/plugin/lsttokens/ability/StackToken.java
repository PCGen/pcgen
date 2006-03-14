package plugin.lsttokens.ability;

import pcgen.core.Ability;
import pcgen.persistence.lst.AbilityLstToken;

/**
 * Deal with STACK token
 */
public class StackToken  implements AbilityLstToken{

	public String getTokenName() {
		return "STACK";
	}

	public boolean parse(Ability ability, String value) {
		ability.setStacks(value);
		return true;
	}
}

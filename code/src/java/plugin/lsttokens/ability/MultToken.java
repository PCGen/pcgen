package plugin.lsttokens.ability;

import pcgen.core.Ability;
import pcgen.persistence.lst.AbilityLstToken;

/**
 * Deals with the MULT token 
 */
public class MultToken  implements AbilityLstToken{

	public String getTokenName() {
		return "MULT";
	}

	public boolean parse(Ability ability, String value) {
		ability.setMultiples(value);
		return true;
	}
}

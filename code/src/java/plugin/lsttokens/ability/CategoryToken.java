package plugin.lsttokens.ability;

import pcgen.core.Ability;
import pcgen.persistence.lst.AbilityLstToken;

/**
 * Deal with CATEGORY token
 */
public class CategoryToken  implements AbilityLstToken{

	public String getTokenName() {
		return "CATEGORY";
	}

	public boolean parse(Ability ability, String value) {
		ability.setCategory(value);
		return true;
	}
}

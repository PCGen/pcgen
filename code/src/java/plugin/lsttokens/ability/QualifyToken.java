package plugin.lsttokens.ability;

import pcgen.core.Ability;
import pcgen.persistence.lst.AbilityLstToken;

/**
 * Deals with the QUALIFY token for Abilities
 */
public class QualifyToken implements AbilityLstToken {

	public String getTokenName() {
		return "QUALIFY";
	}

	public boolean parse(Ability ability, String value) {
		ability.setQualifyString(value);
		return true;
	}
}

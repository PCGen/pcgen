package plugin.lsttokens.ability;

import pcgen.core.Ability;
import pcgen.persistence.lst.AbilityLstToken;

/**
 * This class deals with the BENEFIT Token
 */
public class BenefitToken  implements AbilityLstToken{

	public String getTokenName() {
		return "BENEFIT";
	}

	public boolean parse(Ability ability, String value) {
		ability.setBenefit(value);
		return true;
	}
}

package plugin.lsttokens.ability;

import pcgen.core.Ability;
import pcgen.persistence.lst.AbilityLstToken;

/**
 * Deals with the MODIFYABILITYCHOICE token
 */
public class ModifyabilitychoiceToken  implements AbilityLstToken{

	public String getTokenName() {
		return "MODIFYABILITYCHOICE";
	}

	public boolean parse(Ability ability, String value) {
		ability.setChoiceToModify(value);
		return true;
	}
}

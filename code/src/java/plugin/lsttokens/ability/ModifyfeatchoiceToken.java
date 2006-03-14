package plugin.lsttokens.ability;

import pcgen.core.Ability;
import pcgen.persistence.lst.AbilityLstToken;

/**
 * Deals with the MODIFYFEATCHOICE token
 */
public class ModifyfeatchoiceToken  implements AbilityLstToken{

	public String getTokenName() {
		return "MODIFYFEATCHOICE";
	}

	public boolean parse(Ability ability, String value) {
		ability.setChoiceToModify(value);
		return true;
	}
}

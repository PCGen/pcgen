package plugin.lsttokens.companionmod;

import pcgen.core.character.CompanionMod;
import pcgen.persistence.lst.CompanionModLstToken;

/**
 * Class deals with USEMASTERSKILL Token
 */
public class UsemasterskillToken implements CompanionModLstToken {

	public String getTokenName() {
		return "USEMASTERSKILL";
	}

	public boolean parse(CompanionMod cmpMod, String value) {
		cmpMod.setUseMasterSkill(value.startsWith("Y"));
		return true;
	}
}

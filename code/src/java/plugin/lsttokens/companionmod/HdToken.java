package plugin.lsttokens.companionmod;

import pcgen.core.character.CompanionMod;
import pcgen.persistence.lst.CompanionModLstToken;

/**
 * Class deals with HD Token
 */
public class HdToken implements CompanionModLstToken {

	public String getTokenName() {
		return "HD";
	}

	public boolean parse(CompanionMod cmpMod, String value) {
		cmpMod.setHitDie(Integer.parseInt(value));
		return true;
	}
}

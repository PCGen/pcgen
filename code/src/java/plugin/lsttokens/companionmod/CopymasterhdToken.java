package plugin.lsttokens.companionmod;

import pcgen.core.character.CompanionMod;
import pcgen.persistence.lst.CompanionModLstToken;

/**
 * Class deals with COPYMASTERHP Token
 */
public class CopymasterhdToken implements CompanionModLstToken {

	public String getTokenName() {
		return "COPYMASTERHP";
	}

	public boolean parse(CompanionMod cmpMod, String value) {
		cmpMod.setCopyMasterHP(value);
		return true;
	}
}

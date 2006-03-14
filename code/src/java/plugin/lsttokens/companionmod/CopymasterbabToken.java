package plugin.lsttokens.companionmod;

import pcgen.core.character.CompanionMod;
import pcgen.persistence.lst.CompanionModLstToken;

/**
 * Class deals with COPYMASTERBAB Token
 */
public class CopymasterbabToken implements CompanionModLstToken {

	public String getTokenName() {
		return "COPYMASTERBAB";
	}

	public boolean parse(CompanionMod cmpMod, String value) {
		cmpMod.setCopyMasterBAB(value);
		return true;
	}
}

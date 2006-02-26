package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with BONUSSPELLSTAT Token
 */
public class BonusspellstatToken implements PCClassLstToken {

	public String getTokenName() {
		return "BONUSSPELLSTAT";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setBonusSpellBaseStat(value);
		return true;
	}
}

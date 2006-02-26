package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with ATTACKCYCLE Token
 */
public class AttackcycleToken implements PCClassLstToken {

	public String getTokenName() {
		return "ATTACKCYCLE";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setAttackCycle(value);
		return true;
	}
}

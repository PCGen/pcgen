package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with STARTSKILLPTS Token
 */
public class StartskillptsToken implements PCClassLstToken {

	public String getTokenName() {
		return "STARTSKILLPTS";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setSkillPointFormula(value);
		return true;
	}
}

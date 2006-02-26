package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with SKILLLIST Token
 */
public class SkilllistToken implements PCClassLstToken {

	public String getTokenName() {
		return "SKILLLIST";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setClassSkillString(value);
		return true;
	}
}

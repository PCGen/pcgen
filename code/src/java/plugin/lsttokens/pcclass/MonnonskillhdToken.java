package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with MONNONSKILLHD Token
 */
public class MonnonskillhdToken implements PCClassLstToken {

	public String getTokenName() {
		return "MONNONSKILLHD";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.addBonusList("0|MONNONSKILLHD|NUMBER|" + value);
		return true;
	}
}

package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.utils.FeatParser;

/**
 * Class deals with VFEAT Token
 */
public class VfeatToken implements PCClassLstToken {

	public String getTokenName() {
		return "VFEAT";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.addVirtualFeats(level, FeatParser.parseVirtualFeatList(value));
		return true;
	}
}

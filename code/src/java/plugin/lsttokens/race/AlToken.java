package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with AL Token
 */
public class AlToken implements RaceLstToken {

	public String getTokenName() {
		return "AL";
	}

	public boolean parse(Race race, String value) {
		// Pass into PREALIGN instead
		Prerequisite prereq = new Prerequisite();
		for (int i = 0; i < value.length() ; i++) {
			Prerequisite subreq = new Prerequisite();
			subreq.setKind("align");
			subreq.setKey(value.substring(i, i + 1));
			prereq.addPrerequisite(subreq);
		}
		race.addPreReq(prereq);
		return true;
	}
}

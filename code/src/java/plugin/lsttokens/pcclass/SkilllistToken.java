package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SKILLLIST Token
 */
public class SkilllistToken implements PCClassLstToken {

	public String getTokenName() {
		return "SKILLLIST";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		final StringTokenizer aTok = new StringTokenizer(value, "|");
		int skillCount = 0;

		if (value.indexOf('|') >= 0) {
			try {
				skillCount = Integer.parseInt(aTok.nextToken());
			} catch (NumberFormatException e) {
				Logging.errorPrint("Import error: Expected first value of "
						+ "SKILLLIST token with a | to be a number");
				return false;
			}
		}

		final List<String> skillChoices = new ArrayList<String>();

		while (aTok.hasMoreTokens()) {
			skillChoices.add(aTok.nextToken());
		}

		//Protection against a "" value parameter
		if (skillChoices.size() > 0) {
			pcclass.setClassSkillChoices(skillCount, skillChoices);
		}
		return true;
	}
}

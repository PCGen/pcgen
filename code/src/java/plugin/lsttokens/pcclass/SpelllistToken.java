package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SPELLLIST Token
 */
public class SpelllistToken implements PCClassLstToken {

	public String getTokenName() {
		return "SPELLLIST";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		final StringTokenizer aTok = new StringTokenizer(value, "|");
		int spellCount = 0;
		
		if (value.indexOf('|') >= 0) {
			try {
				spellCount = Integer.parseInt(aTok.nextToken());
			} catch (NumberFormatException e) {
				Logging.errorPrint("Import error: Expected first value of "
						+ "SPELLLIST token with a | to be a number");
				return false;
			}
		}
		
		final List<String> spellChoices = new ArrayList<String>();
		
		while (aTok.hasMoreTokens()) {
			spellChoices.add(aTok.nextToken());
		}
		
		//Protection against a "" value parameter
		if (spellChoices.size() > 0) {
			pcclass.setClassSpellChoices(spellCount, spellChoices);
		}
		return true;
	}
}

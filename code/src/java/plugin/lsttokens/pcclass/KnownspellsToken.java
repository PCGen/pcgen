package plugin.lsttokens.pcclass;

import java.util.StringTokenizer;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with KNOWNSPELLS Token
 */
public class KnownspellsToken implements PCClassLstToken {

	public String getTokenName() {
		return "KNOWNSPELLS";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);

		while (aTok.hasMoreTokens()) {
			pcclass.addKnownSpell(aTok.nextToken());
		}
		return true;
	}
}

package plugin.lsttokens.pcclass;

import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with FEATAUTO Token
 */
public class FeatautoToken implements PCClassLstToken {

	public String getTokenName() {
		return "FEATAUTO";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		final StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);
		
		while (aTok.hasMoreTokens()) {
			final String fName = aTok.nextToken();

			if (fName.startsWith(".CLEAR")) {
				if (fName.startsWith(".CLEAR.")) {
					pcclass.removeFeatAuto(fName.substring(7));
				} else {
					pcclass.clearFeatAutos();
				}
			} else {
				pcclass.addFeatAuto(level, fName);
			}
		}
		return true;
	}
}

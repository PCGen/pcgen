package plugin.lsttokens.pcclass;

import java.util.StringTokenizer;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with ADDDOMAINS Token
 */
public class AdddomainsToken implements PCClassLstToken {

	public String getTokenName() {
		return "ADDDOMAINS";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		final StringTokenizer aTok = new StringTokenizer(value, ".", false);

		while (aTok.hasMoreTokens())
		{
			pcclass.addAddDomain( level, aTok.nextToken() );
		}

		return true;
	}
}


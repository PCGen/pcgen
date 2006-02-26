package plugin.lsttokens.deity;

import pcgen.core.Deity;
import pcgen.persistence.lst.DeityLstToken;

/**
 * Class deals with WORSHIPPERS Token
 */
public class WorshippersToken implements DeityLstToken{

	public String getTokenName() {
		return "WORSHIPPERS";
	}

	public boolean parse(Deity deity, String value) {
		deity.setWorshippers(value);
		return true;
	}
}

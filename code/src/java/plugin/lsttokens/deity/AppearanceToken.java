package plugin.lsttokens.deity;

import pcgen.core.Deity;
import pcgen.persistence.lst.DeityLstToken;

/**
 * Class deals with APPEARANCE Token
 */
public class AppearanceToken implements DeityLstToken{

	public String getTokenName() {
		return "APPEARANCE";
	}

	public boolean parse(Deity deity, String value) {
		deity.setAppearance(value);
		return true;
	}
}

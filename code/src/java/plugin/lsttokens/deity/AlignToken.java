package plugin.lsttokens.deity;

import pcgen.core.Deity;
import pcgen.persistence.lst.DeityLstToken;

/**
 * Class deals with ALIGN Token
 */
public class AlignToken implements DeityLstToken{

	public String getTokenName() {
		return "ALIGN";
	}

	public boolean parse(Deity deity, String value) {
		deity.setAlignment(value);
		return true;
	}
}

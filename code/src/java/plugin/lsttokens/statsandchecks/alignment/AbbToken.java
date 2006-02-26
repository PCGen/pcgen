package plugin.lsttokens.statsandchecks.alignment;

import pcgen.core.PCAlignment;
import pcgen.persistence.lst.PCAlignmentLstToken;

/**
 * Class deals with ABB Token
 */
public class AbbToken implements PCAlignmentLstToken{

	public String getTokenName() {
		return "ABB";
	}

	public boolean parse(PCAlignment align, String value) {
		align.setKeyName(value);
		return true;
	}
}

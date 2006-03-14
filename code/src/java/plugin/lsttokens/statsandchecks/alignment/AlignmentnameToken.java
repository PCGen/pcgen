package plugin.lsttokens.statsandchecks.alignment;

import pcgen.core.PCAlignment;
import pcgen.persistence.lst.PCAlignmentLstToken;

/**
 * Class deals with ALIGNMENTNAME Token
 */
public class AlignmentnameToken implements PCAlignmentLstToken{

	public String getTokenName() {
		return "ALIGNMENTNAME";
	}

	public boolean parse(PCAlignment align, String value) {
		align.setName(value);
		return true;
	}
}

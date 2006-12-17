package plugin.lsttokens.statsandchecks.alignment;

import pcgen.core.PCAlignment;
import pcgen.persistence.lst.PCAlignmentLstToken;

/**
 * Class deals with VALIDFORDEITY Token
 */
public class ValidfordeityToken implements PCAlignmentLstToken
{

	public String getTokenName()
	{
		return "VALIDFORDEITY";
	}

	public boolean parse(PCAlignment align, String value)
	{
		align.setValidForDeity(value.toUpperCase().startsWith("Y"));
		return true;
	}
}

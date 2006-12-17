package plugin.lsttokens.statsandchecks.alignment;

import pcgen.core.PCAlignment;
import pcgen.persistence.lst.PCAlignmentLstToken;

/**
 * Class deals with ABB Token for pc alignment
 */
public class AbbToken implements PCAlignmentLstToken
{

	/**
	 * Return the token name
	 * @return token name
	 */
	public String getTokenName()
	{
		return "ABB";
	}

	/**
	 * Parse the abbreviation token
	 * @param align 
	 * @param value 
	 * @return true
	 */
	public boolean parse(PCAlignment align, String value)
	{
		align.setKeyName(value);
		return true;
	}
}

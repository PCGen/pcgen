package plugin.lsttokens.statsandchecks.stat;

import pcgen.core.PCStat;
import pcgen.persistence.lst.PCStatLstToken;

/**
 * Class deals with ABB Token for pc stat
 */
public class AbbToken implements PCStatLstToken
{

	/**
	 * Get token name
	 * @return token name
	 */
	public String getTokenName()
	{
		return "ABB";
	}

	/**
	 * Parse ABB token
	 * @param stat 
	 * @param value 
	 * @return true
	 */
	public boolean parse(PCStat stat, String value)
	{
		stat.setAbb(value);
		return true;
	}
}

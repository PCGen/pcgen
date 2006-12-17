package plugin.lsttokens.gamemode.unitset;

import pcgen.core.UnitSet;
import pcgen.persistence.lst.UnitSetLstToken;

/**
 * Class deals with WEIGHTUNIT Token
 */
public class WeightunitToken implements UnitSetLstToken
{

	/**
	 * Get token name
	 * @return token name
	 */
	public String getTokenName()
	{
		return "WEIGHTUNIT";
	}

	/**
	 * Parse WEIGHTUNIT token
	 * @param unitSet 
	 * @param value 
	 * @return true
	 */
	public boolean parse(UnitSet unitSet, String value)
	{
		unitSet.setWeightUnit(value);
		return true;
	}
}

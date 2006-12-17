package plugin.lsttokens.gamemode.unitset;

import pcgen.core.UnitSet;
import pcgen.persistence.lst.UnitSetLstToken;

/**
 * Class deals with DISTANCEUNIT Token
 */
public class DistanceunitToken implements UnitSetLstToken
{

	public String getTokenName()
	{
		return "DISTANCEUNIT";
	}

	public boolean parse(UnitSet unitSet, String value)
	{
		unitSet.setDistanceUnit(value);
		return true;
	}
}

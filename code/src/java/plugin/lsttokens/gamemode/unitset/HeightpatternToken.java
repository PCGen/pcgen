package plugin.lsttokens.gamemode.unitset;

import pcgen.core.UnitSet;
import pcgen.persistence.lst.UnitSetLstToken;

/**
 * Class deals with HEIGHTPATTERN Token
 */
public class HeightpatternToken implements UnitSetLstToken
{

	public String getTokenName()
	{
		return "HEIGHTPATTERN";
	}

	public boolean parse(UnitSet unitSet, String value)
	{
		unitSet.setHeightDisplayPattern(value);
		return true;
	}
}

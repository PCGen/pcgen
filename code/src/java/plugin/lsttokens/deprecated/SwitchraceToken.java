package plugin.lsttokens.deprecated;

import pcgen.core.PObject;
import pcgen.core.character.CompanionMod;
import pcgen.persistence.lst.CompanionModLstToken;
import pcgen.persistence.lst.DeprecatedToken;

import java.util.StringTokenizer;

/**
 * Class deals with SWITCHRACE: Token
 */
public class SwitchraceToken implements CompanionModLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "SWITCHRACE";
	}

	public boolean parse(CompanionMod cmpMod, String value)
	{
		try
		{
			final StringTokenizer aTok = new StringTokenizer(value, "|", false);
			final String currT = aTok.nextToken();
			final String toT = aTok.nextToken();
			cmpMod.getSwitchRaceMap().put(currT.toUpperCase(),
				toT.toUpperCase());
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public String getMessage(PObject obj, String value)
	{
		return getTokenName() + " in CompanionMod files is deprecated: "
				+ value + ".  You should be using RACETYPEs instead";
	}
}

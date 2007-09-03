package plugin.lsttokens.deprecated;

import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with MFEAT Token
 */
public class MfeatToken implements RaceLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "MFEAT";
	}

	public boolean parse(Race race, String value)
	{
		race.setMFeatList(value);
		return true;
	}

	public String getMessage(PObject obj, String value)
	{
		return getTokenName()
			+ " is deprecated, because Default Monster Mode is deprecated";
	}
}

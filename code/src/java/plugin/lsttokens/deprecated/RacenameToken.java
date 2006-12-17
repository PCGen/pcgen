package plugin.lsttokens.deprecated;

import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with RACENAME Token
 */
public class RacenameToken implements RaceLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "RACENAME";
	}

	public boolean parse(Race race, String value)
	{
		race.setName(value);
		return true;
	}

	public String getMessage(PObject obj, String value)
	{
		return "This was a PCGen 2.X Tag.  Use OUTPUTNAME instead";
	}
}

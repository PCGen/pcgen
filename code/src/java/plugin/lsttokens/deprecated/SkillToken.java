package plugin.lsttokens.deprecated;

import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with SKILL Token
 */
public class SkillToken implements RaceLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "SKILL";
	}

	public boolean parse(Race race, String value)
	{
		race.setBonusSkillList(value);
		return true;
	}

	public String getMessage(PObject obj, String value)
	{
		return "SKILL in Race Files is deprecated.  Use BONUS|SKILL instead";
	}
}

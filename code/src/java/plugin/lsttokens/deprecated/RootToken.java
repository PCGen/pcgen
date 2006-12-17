package plugin.lsttokens.deprecated;

import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.SkillLstToken;

/**
 * Class deals with ROOT Token
 */
public class RootToken implements SkillLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "ROOT";
	}

	public boolean parse(Skill skill, String value)
	{
		skill.setRootName(value);
		return true;
	}

	public String getMessage(PObject obj, String value)
	{
		return "ROOT is a non-functioning Token - predates COST, TYPE breakdowns for Skills";
	}
}

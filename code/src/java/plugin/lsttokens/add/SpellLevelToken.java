package plugin.lsttokens.add;

import pcgen.core.PObject;
import pcgen.persistence.lst.AddLstToken;

public class SpellLevelToken implements AddLstToken
{

	public boolean parse(PObject target, String value, int level)
	{
		target.addAddList(level, getTokenName() + ":" + value);
		return true;
	}

	public String getTokenName()
	{
		return "SPELLLEVEL";
	}
}

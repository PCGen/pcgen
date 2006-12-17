package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with MONSKILL Token
 */
public class MonskillToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "MONSKILL";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass
			.addBonusList("0|MONSKILLPTS|NUMBER|" + value + "|PRELEVELMAX:1");
		return true;
	}
}

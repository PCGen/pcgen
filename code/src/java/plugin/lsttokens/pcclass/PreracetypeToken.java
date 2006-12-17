package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with PRERACETYPE Token
 */
public class PreracetypeToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "PRERACETYPE";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setPreRaceType(value);
		return true;
	}
}

package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with MODTOSKILLS Token
 */
public class ModtoskillsToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "MODTOSKILLS";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setModToSkills(!"No".equalsIgnoreCase(value));
		return true;
	}
}

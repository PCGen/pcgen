package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with MAXLEVEL Token
 */
public class MaxlevelToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "MAXLEVEL";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		if ("NOLIMIT".equalsIgnoreCase(value))
		{
			pcclass.setMaxLevel(PCClass.NO_LEVEL_LIMIT);
			return true;
		}
		try
		{
			pcclass.setMaxLevel(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}
}

package plugin.lsttokens.subclass;

import pcgen.core.SubClass;
import pcgen.persistence.lst.SubClassLstToken;

/**
 * Class deals with COST Token
 */
public class CostToken implements SubClassLstToken
{

	public String getTokenName()
	{
		return "COST";
	}

	public boolean parse(SubClass subclass, String value)
	{
		try
		{
			subclass.setCost(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}
}

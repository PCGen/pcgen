package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "VISIBLE";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		if (!value.toUpperCase().startsWith("Y"))
		{
			pcclass.setVisibility(Visibility.HIDDEN);
		} //Assume DEFAULT is the DEFAULT :)
		return true;
	}
}

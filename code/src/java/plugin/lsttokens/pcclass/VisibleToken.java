package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;
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
		String visType = value.toUpperCase();
		if (visType.equals("Y"))
		{
			pcclass.setVisibility(Visibility.DEFAULT);
		}
		else
		{
			if (!"NO".equals(visType))
			{
				Logging.errorPrint("Unexpected value used in " + getTokenName()
						+ " in Class");
				Logging.errorPrint(" " + visType + " is not a valid value for "
						+ getTokenName());
				Logging.errorPrint(" Valid values in Class are NO and YES");
				return false;
			}
			pcclass.setVisibility(Visibility.HIDDEN);
		}
		return true;
	}
}

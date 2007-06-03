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
		if (visType.startsWith("Y"))
		{
			if (!"YES".equals(visType))
			{
				Logging.deprecationPrint("Abbreviation used in "
					+ getTokenName() + " in Class");
				Logging.deprecationPrint(" " + visType
					+ " is not a valid value for " + getTokenName());
				Logging
					.deprecationPrint(" assuming you meant YES, please use YES (exact String, upper case) in the LST file");
				Logging.deprecationPrint(" This will break after PCGen 5.14");
			}
			pcclass.setVisibility(Visibility.DEFAULT);
		}
		else
		{
			if (!"NO".equals(visType))
			{
				Logging.deprecationPrint("Unexpected value used in "
					+ getTokenName() + " in Class");
				Logging.deprecationPrint(" " + visType
					+ " is not a valid value for " + getTokenName());
				Logging
					.deprecationPrint(" Valid values in Class are NO and YES");
				Logging
					.deprecationPrint(" assuming you meant NO, please use NO (exact String, upper case) in the LST file");
			}
			pcclass.setVisibility(Visibility.HIDDEN);
		}
		return true;
	}
}

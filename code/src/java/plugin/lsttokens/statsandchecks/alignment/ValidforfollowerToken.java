package plugin.lsttokens.statsandchecks.alignment;

import pcgen.core.PCAlignment;
import pcgen.persistence.lst.PCAlignmentLstToken;
import pcgen.util.Logging;

/**
 * Class deals with VALIDFORFOLLOWER Token
 */
public class ValidforfollowerToken implements PCAlignmentLstToken
{

	public String getTokenName()
	{
		return "VALIDFORFOLLOWER";
	}

	public boolean parse(PCAlignment align, String value)
	{
		boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar =='Y')
		{
			if (!value.equalsIgnoreCase("YES"))
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the " + getTokenName());
				Logging.errorPrint("Abbreviations will fail after PCGen 5.12");
			}
			set = true;
		}
		else 
		{
			if (value.equalsIgnoreCase("NO"))
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName());
				Logging.errorPrint("Abbreviations will fail after PCGen 5.12");
			}
			set = false;
		}
		align.setValidForFollower(set);
		return true;
	}
}

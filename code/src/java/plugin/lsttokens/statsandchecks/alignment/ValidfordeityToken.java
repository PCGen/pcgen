package plugin.lsttokens.statsandchecks.alignment;

import pcgen.core.PCAlignment;
import pcgen.persistence.lst.PCAlignmentLstToken;
import pcgen.util.Logging;

/**
 * Class deals with VALIDFORDEITY Token
 */
public class ValidfordeityToken implements PCAlignmentLstToken
{

	public String getTokenName()
	{
		return "VALIDFORDEITY";
	}

	public boolean parse(PCAlignment align, String value)
	{
		boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar =='Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName());
				return false;
			}
			set = true;
		}
		else 
		{
			if (firstChar != 'N' && firstChar != 'n'
				&& !value.equalsIgnoreCase("NO"))
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName());
				return false;
			}
			set = false;
		}
		align.setValidForDeity(set);
		return true;
	}
}

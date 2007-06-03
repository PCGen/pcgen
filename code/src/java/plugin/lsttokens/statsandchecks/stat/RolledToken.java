package plugin.lsttokens.statsandchecks.stat;

import pcgen.core.PCStat;
import pcgen.persistence.lst.PCStatLstToken;
import pcgen.util.Logging;

/**
 * Class deals with PENALTYVAR Token
 */
public class RolledToken implements PCStatLstToken
{

	public String getTokenName()
	{
		return "ROLLED";
	}

	public boolean parse(PCStat stat, String value)
	{
		boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar =='Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.deprecationPrint("You should use 'YES' or 'NO' as the " + getTokenName());
				Logging.deprecationPrint("Abbreviations will fail after PCGen 5.14");
			}
			set = true;
		}
		else 
		{
			if (firstChar != 'N' && firstChar != 'n'
				&& !value.equalsIgnoreCase("NO"))
			{
				Logging.deprecationPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName());
				Logging.deprecationPrint("Abbreviations will fail after PCGen 5.14");
			}
			set = false;
		}
		stat.setRolled(set);
		return true;
	}
}

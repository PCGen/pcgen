/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class NameispiLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "NAMEISPI";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.deprecationPrint("You should use 'YES' or 'NO' as the "
					+ getTokenName());
				Logging
					.deprecationPrint("Abbreviations will fail after PCGen 5.14");
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
				Logging
					.deprecationPrint("Abbreviations will fail after PCGen 5.14");
			}
			set = false;
		}
		obj.setNameIsPI(set);
		return true;
	}
}

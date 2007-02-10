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
public class DescispiLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "DESCISPI";
	}

	public boolean parse(PObject obj, String value, int anInt)
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
		obj.setDescIsPI(set);
		return true;
	}
}

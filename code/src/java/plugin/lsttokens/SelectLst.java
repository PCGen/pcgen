package plugin.lsttokens;

import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SELECT Token
 */
public class SelectLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "SELECT";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		try
		{
			int select = Integer.parseInt(value);
			if (select <= 0)
			{
				Logging.errorPrint(getTokenName()
						+ "must be a positive integer: " + value);
				return false;
			}
			obj.setSelect(select);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}
}
